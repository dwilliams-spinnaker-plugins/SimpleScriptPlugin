package net.port8080.spinnaker.plugins.stage.script.simple;

import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class BindArtifact {
    @Nullable
    private String expectedArtifactId;

//    @Nullable public String getExpectedArtifactId() {
//        return expectedArtifactId;
//    }

    @Nullable private Artifact artifact;

//    @Nullable public Artifact getArtifact() {
//        return artifact;
//    }
}
