package net.port8080.spinnaker.plugins.stage.script.simple;

import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class BindArtifact { // FIXME: Not needed
    @Nullable
    private String expectedArtifactId;

    @Nullable private Artifact artifact;
}
