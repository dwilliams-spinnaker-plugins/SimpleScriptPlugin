package net.port8080.spinnaker.plugins.stage.script.simple;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

@Builder(builderClassName = "SimpleScriptContextBuilder", toBuilder = true)
@JsonDeserialize(builder = SimpleScriptContext.SimpleScriptContextBuilder.class)
@Value
public class SimpleScriptContext implements ScriptContext {
    @Nullable
    String script;

    Source source;

    String scriptArtifactId;
    Artifact scriptArtifact;
    String scriptArtifactAccount;

    List<String> requiredArtifactIds;
    List<BindArtifact> requiredArtifacts;

    @Builder.Default
    boolean skipExpressionEvaluation = false;

    @Nullable
    @Override
    public String getScript() {
        return script;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class SimpleScriptContextBuilder {}
}
