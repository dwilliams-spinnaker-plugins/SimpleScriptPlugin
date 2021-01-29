package net.port8080.spinnaker.plugins.stage.script.simple;

//import com.google.common.collect.ImmutableMap;
import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import static com.google.common.base.Preconditions.checkArgument;
//import static com.google.common.collect.ImmutableList.toImmutableList;
//import static java.util.Collections.emptyList;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Streams;
//import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
//import com.netflix.spinnaker.kork.artifacts.model.Artifact;
//import com.netflix.spinnaker.kork.core.RetrySupport;
//import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
//import com.netflix.spinnaker.orca.clouddriver.OortService;
//import com.netflix.spinnaker.orca.clouddriver.tasks.manifest.ManifestContext.BindArtifact;
//import com.netflix.spinnaker.orca.clouddriver.utils.CloudProviderAware;
//import com.netflix.spinnaker.orca.jackson.OrcaObjectMapper;
//import com.netflix.spinnaker.orca.pipeline.expressions.PipelineExpressionEvaluator;
//import com.netflix.spinnaker.orca.pipeline.util.ArtifactUtils;
//import com.netflix.spinnaker.orca.pipeline.util.ContextParameterProcessor;
//import java.io.InputStream;
//import java.time.Duration;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.Supplier;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
////import org.yaml.snakeyaml.Yaml;
////import org.yaml.snakeyaml.constructor.SafeConstructor;
//import retrofit.client.Response;

@Component
@NonnullByDefault
public final class SimpleScriptTask implements Task {
    public static final String TASK_NAME = "runSimpleScript";

    @Nonnull
    @Override
    public TaskResult execute(@Nonnull StageExecution stage) {
        SimpleScriptContext context = stage.mapTo(SimpleScriptContext.class);
        Bindings outputs = null;

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript"); // move to context soon
            engine.put("context", context);
            outputs = (Bindings)engine.eval(context.getScript());

            // Check for protected values
        } catch (Exception ex) {
            ex.printStackTrace(); // FIXME: Actually log this and fail the stage.
        }

        return TaskResult.builder(ExecutionStatus.SUCCEEDED).context(outputs).outputs(outputs).build();
    }
}
