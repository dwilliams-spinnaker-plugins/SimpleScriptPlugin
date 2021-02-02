package net.port8080.spinnaker.plugins.stage.script.simple;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.netflix.spinnaker.kork.core.RetrySupport;
import com.netflix.spinnaker.orca.clouddriver.OortService;
import com.netflix.spinnaker.orca.pipeline.expressions.PipelineExpressionEvaluator;
import com.netflix.spinnaker.orca.pipeline.util.ArtifactUtils;
import com.netflix.spinnaker.orca.pipeline.util.ContextParameterProcessor;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import retrofit.client.Response;

// Example code at the following URLs:
// - https://github.com/spinnaker/orca/blob/b2e18aa633f836864125c34357d2bac77ff65052/orca-clouddriver/src/main/groovy/com/netflix/spinnaker/orca/clouddriver/tasks/manifest/ResolveDeploySourceManifestTask.java
// - https://github.com/spinnaker/orca/blob/b2e18aa633f836864125c34357d2bac77ff65052/orca-clouddriver/src/main/groovy/com/netflix/spinnaker/orca/clouddriver/tasks/manifest/ManifestEvaluator.java

@Component
@NonnullByDefault
public final class ResolveScriptTask implements Task {
    public static final String TASK_NAME = "resolveScript";

    private final Logger logger = LoggerFactory.getLogger(ResolveScriptTask.class);

    private final ArtifactUtils artifactUtils;
    private final ContextParameterProcessor contextParameterProcessor;
    private final OortService oortService;
    private final RetrySupport retrySupport;

    @Autowired
    public ResolveScriptTask(
            ArtifactUtils artifactUtils,
            ContextParameterProcessor contextParameterProcessor,
            OortService oortService,
            RetrySupport retrySupport) {
        this.artifactUtils = artifactUtils;
        this.contextParameterProcessor = contextParameterProcessor;
        this.oortService = oortService;
        this.retrySupport = retrySupport;
    }

    @Nonnull
    @Override
    public TaskResult execute(@Nonnull StageExecution stage) {
        logger.info("SimpleScriptPlugin.ResolveScriptTask.execute()");
        SimpleScriptContext context = stage.mapTo(SimpleScriptContext.class);

        ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<String, Object>();

        builder.put("script", getScript(stage, context));
        //builder.put("requiredArtifacts", getRequiredArtifacts(stage, context)); // FIXME: Not needed
        //builder.put("optionalArtifacts", ImmutableList.copyOf(artifactUtils.getArtifacts(stage))); // FIXME: Not needed

        ImmutableMap<String, Object> outputs = builder.build();

        return TaskResult.builder(ExecutionStatus.SUCCEEDED).context(outputs).outputs(outputs).build();
    }

    private String getScript(StageExecution stage, ScriptContext context) {
        String finalScript = "";
        switch (context.getSource()) {
            case Artifact:
                finalScript = getScriptFromArtifact(stage, context);
                break;
            case Text:
                finalScript = getScriptFromText(context);
                break;
            default:
                throw new IllegalStateException("Unknown ManifestContext.Source " + context.getSource());
        }
        logger.info("SimpleScriptPlugin.ResolveScriptTask.getScript() finalScript: {}", finalScript);
        return finalScript;
    }

    private String getScriptFromText(ScriptContext context) {
        String textScript =
                Optional.ofNullable(context.getScript())
                        .orElseThrow(() -> new IllegalArgumentException("No text script was specified."));
        return textScript;
    }

    private String getScriptFromArtifact(StageExecution stage, ScriptContext context) {
        Artifact scriptArtifact = getScriptArtifact(stage, context);

        String rawScript = retrySupport.retry(fetchScript(scriptArtifact), 10, Duration.ofMillis(200), true);

        logger.info("SimpleScriptPlugin.ResolveScriptTask.getScriptFromArtifact() rawScript: {}", rawScript);

        if (context.isSkipExpressionEvaluation()) {
            logger.info("SimpleScriptPlugin.ResolveScriptTask.getScriptFromArtifact() Skipping SpEL Evaluation");
            return rawScript;
        }

        String evaldScript = getSpelEvaluatedScript(rawScript, stage);

        logger.info("SimpleScriptPlugin.ResolveScriptTask.getScriptFromArtifact() evaldScript: {}", evaldScript);

        return evaldScript;
    }

    private Artifact getScriptArtifact(StageExecution stage, ScriptContext context) {
        Artifact scriptArtifact =
                Optional.ofNullable(
                        artifactUtils.getBoundArtifactForStage(
                                stage, context.getScriptArtifactId(), context.getScriptArtifact()
                        )
                )
                        // Once the legacy artifacts feature is removed, all trigger expected artifacts will be
                        // required to define an account up front.
                        .map(
                                artifact ->
                                        ArtifactUtils.withAccount(artifact, context.getScriptArtifactAccount())
                        )
                        .orElseThrow(() -> new IllegalArgumentException("No script artifact was specified."));

        checkArgument(
                scriptArtifact.getArtifactAccount() != null,
                "No script artifact account was specified.");

        return scriptArtifact;
    }

    private Supplier<String> fetchScript(Artifact scriptArtifact) {
        return () -> {
            Response scriptText = oortService.fetchArtifact(scriptArtifact);
            try (InputStream body = scriptText.getBody().in()) {
                //return yamlParser.get().loadAll(body);
                //return body.toString();
                return CharStreams.toString(new InputStreamReader(body));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }

    private String getSpelEvaluatedScript(String rawScript, StageExecution stage) {
        Map<String, Object> processorInput = ImmutableMap.of("script", rawScript);

        Map<String, Object> processorResult =
                contextParameterProcessor.process(
                        processorInput,
                        contextParameterProcessor.buildExecutionContext(stage),
                        /* allowUnknownKeys= */ true);

        if ((boolean) stage.getContext().getOrDefault("failOnFailedExpressions", false)
                && processorResult.containsKey(PipelineExpressionEvaluator.SUMMARY)) {
            throw new IllegalStateException(
                    String.format(
                            "Failure evaluating manifest expressions: %s",
                            processorResult.get(PipelineExpressionEvaluator.SUMMARY)));
        }

        return (String) processorResult.get("script");
    }

//    private ImmutableList<Artifact> getRequiredArtifacts(StageExecution stage, ScriptContext context) {
//        Stream<Artifact> requiredArtifactsFromId =
//                Optional.ofNullable(context.getRequiredArtifactIds()).orElse(emptyList()).stream()
//                        .map(artifactId -> resolveRequiredArtifactById(stage, artifactId));
//
//        Stream<Artifact> requiredArtifacts =
//                Optional.ofNullable(context.getRequiredArtifacts()).orElse(emptyList()).stream()
//                        .map(artifact -> resolveRequiredArtifact(stage, artifact));
//
//        return Streams.concat(requiredArtifactsFromId, requiredArtifacts).collect(toImmutableList());
//    }

//    private Artifact resolveRequiredArtifactById(StageExecution stage, String artifactId) {
//        return Optional.ofNullable(artifactUtils.getBoundArtifactForId(stage, artifactId))
//                .orElseThrow(
//                        () ->
//                                new IllegalStateException(
//                                        String.format(
//                                                "No artifact with id %s could be found in the pipeline context.",
//                                                artifactId)));
//    }

//    private Artifact resolveRequiredArtifact(StageExecution stage, BindArtifact artifact) {
//        return Optional.ofNullable(
//                artifactUtils.getBoundArtifactForStage(
//                        stage, artifact.getExpectedArtifactId(), artifact.getArtifact()))
//                .orElseThrow(
//                        () ->
//                                new IllegalStateException(
//                                        String.format(
//                                                "No artifact with id %s could be found in the pipeline context.",
//                                                artifact.getExpectedArtifactId())));
//    }
}

