package net.port8080.spinnaker.plugins.stage.script.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.netflix.spinnaker.kork.artifacts.model.Artifact;
//import java.util.List;
//import java.util.Map;
//import javax.annotation.Nullable;
//import lombok.Data;

public interface ScriptContext {
    Source getSource();

    String getScriptArtifactId();

    Artifact getScriptArtifact();

    String getScriptArtifactAccount();

    List<String> getRequiredArtifactIds();

    List<BindArtifact> getRequiredArtifacts();

    boolean isSkipExpressionEvaluation();

    /**
     * @return A manifest provided as direct text input in the stage definition. Deploy and Patch
     *     Manifest stages have differently named model elements describing this one concept, so for
     *     backwards compatibility we must map their individual model elements to use them generally.
     */
    @Nullable
    String getScript();
    //List<Map<Object, Object>> getManifests();

    enum Source {
        @JsonProperty("text")
        Text,

        @JsonProperty("artifact")
        Artifact
    }

//    @Data
//    class BindArtifact {
//        @Nullable private String expectedArtifactId;
//
//        @Nullable public String getExpectedArtifactId() {
//            return expectedArtifactId;
//        }
//
//        @Nullable private Artifact artifact;
//
//        @Nullable public Artifact getArtifact() {
//            return artifact;
//        }
//    }
}