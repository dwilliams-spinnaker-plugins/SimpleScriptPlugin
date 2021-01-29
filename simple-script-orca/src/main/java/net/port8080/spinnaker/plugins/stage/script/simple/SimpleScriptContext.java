package net.port8080.spinnaker.plugins.stage.script.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

@Builder(builderClassName = "SimpleScriptContextBuilder", toBuilder = true)
@JsonDeserialize(builder = SimpleScriptContext.SimpleScriptContextBuilder.class)
@Value
public class SimpleScriptContext implements ScriptContext {
    //@Nullable private List<Map<Object, Object>> manifests;
    @Nullable private String script;

//    @Builder.Default @Nonnull
//    private TrafficManagement trafficManagement = TrafficManagement.builder().build();

    private Source source;

    private String scriptArtifactId;
    private Artifact scriptArtifact;
    private String scriptArtifactAccount;

    private List<String> requiredArtifactIds;
    private List<BindArtifact> requiredArtifacts;


//    public Source getSource() {
//        return source;
//    }
//
//    public String getScriptArtifactId() {
//        return scriptArtifactId;
//    }
//
//    public Artifact getScriptArtifact() {
//        return scriptArtifact;
//    }
//
//    public String getScriptArtifactAccount() {
//        return scriptArtifactAccount;
//    }
//
//    public List<String> getRequiredArtifactIds() {
//        return requiredArtifactIds;
//    }
//
//    public List<BindArtifact> getRequiredArtifacts() {
//        return requiredArtifacts;
//    }

    @Builder.Default private boolean skipExpressionEvaluation = false;

//    @Override
//    public boolean isSkipExpressionEvaluation() {
//        return skipExpressionEvaluation;
//    }

//    @Builder(builderClassName = "TrafficManagementBuilder", toBuilder = true)
//    @JsonDeserialize(builder = DeployManifestContext.TrafficManagement.TrafficManagementBuilder.class)
//    @Value
//    public static class TrafficManagement {
//        @Builder.Default private boolean enabled = false;
//        @Nonnull @Builder.Default private Options options = Options.builder().build();
//
//        @Builder(builderClassName = "OptionsBuilder", toBuilder = true)
//        @JsonDeserialize(builder = DeployManifestContext.TrafficManagement.Options.OptionsBuilder.class)
//        @Value
//        public static class Options {
//            @Builder.Default private boolean enableTraffic = false;
//            @Builder.Default private List<String> services = Collections.emptyList();
//            @Builder.Default private ManifestStrategyType strategy = ManifestStrategyType.NONE;
//
//            @JsonPOJOBuilder(withPrefix = "")
//            public static class OptionsBuilder {}
//        }
//
//        public enum ManifestStrategyType {
//            @JsonProperty("redblack")
//            RED_BLACK,
//
//            @JsonProperty("highlander")
//            HIGHLANDER,
//
//            @JsonProperty("none")
//            NONE
//        }
//
//        @JsonPOJOBuilder(withPrefix = "")
//        public static class TrafficManagementBuilder {}
//    }

    //    @Override
//    public List<Map<Object, Object>> getManifests() {
//        return manifests;
//    }
    @Override
    public String getScript() {
        return script;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class SimpleScriptContextBuilder {}
}
