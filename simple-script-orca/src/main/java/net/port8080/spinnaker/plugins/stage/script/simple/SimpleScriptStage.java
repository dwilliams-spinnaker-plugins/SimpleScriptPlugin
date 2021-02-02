package net.port8080.spinnaker.plugins.stage.script.simple;

import com.netflix.spinnaker.orca.api.pipeline.graph.StageDefinitionBuilder;
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import org.pf4j.Extension;

import javax.annotation.Nonnull;

@Extension
public class SimpleScriptStage implements StageDefinitionBuilder {

    @Override
    public void taskGraph(@Nonnull StageExecution stage, @Nonnull TaskNode.Builder builder) {
        builder.withTask(ResolveScriptTask.TASK_NAME, ResolveScriptTask.class);
        builder.withTask(SimpleScriptTask.TASK_NAME, SimpleScriptTask.class);
    }
}
