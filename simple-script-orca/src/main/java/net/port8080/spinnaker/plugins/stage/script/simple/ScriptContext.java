package net.port8080.spinnaker.plugins.stage.script.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;

import javax.annotation.Nullable;
import java.util.List;

public interface ScriptContext {
    Source getSource();

    String getScriptArtifactId();

    Artifact getScriptArtifact();

    String getScriptArtifactAccount();

    List<String> getRequiredArtifactIds();

    List<BindArtifact> getRequiredArtifacts();

    boolean isSkipExpressionEvaluation();

    @Nullable
    String getScript();

    enum Source {
        @JsonProperty("text")
        Text,

        @JsonProperty("artifact")
        Artifact
    }
}
