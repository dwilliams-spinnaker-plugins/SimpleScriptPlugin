package net.port8080.spinnaker.plugins.stage.script.simple;

import com.google.common.collect.ImmutableMap;
import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NonnullByDefault
public final class SimpleScriptTask implements Task {
    public static final String TASK_NAME = "runSimpleScript";

    private final Logger logger = LoggerFactory.getLogger(SimpleScriptTask.class);

    @Nonnull
    @Override
    public TaskResult execute(@Nonnull StageExecution stage) {
        SimpleScriptContext context = stage.mapTo(SimpleScriptContext.class);

        ExecutionStatus executionStatus = ExecutionStatus.SUCCEEDED;
        ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<String, Object>();

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript"); // move to context soon
            engine.put("stage_context", context); // FIXME: How to get pipeline context?
//            Bindings evalOutputs = (Bindings)engine.eval(context.getScript());
//            if(evalOutputs != null) {
//                for (Map.Entry<String, Object> entry : evalOutputs.entrySet()) {
//                    builder.put(entry.getKey(), entry.getValue());
//                }
//            }
            Object evalOutput = engine.eval(context.getScript());
            if(evalOutput != null) {
                builder.put("script_output", evalOutput);
            }
        } catch (Exception ex) {
            // FIXME: This should just rethrow the exception and let the above class handle gracefully
            //        Really, need to figure out the 'spinnaker task' way to handle exceptions here.
            logger.warn("SimpleScriptTask threw an exception: {}", ex.getMessage());
            builder.put("exception", ex.getMessage()); // FIXME: Can't serialize to JSON the exception, use message.
            executionStatus = ExecutionStatus.TERMINAL;
        }

        ImmutableMap<String, Object> outputs = builder.build();

        return TaskResult.builder(executionStatus).outputs(outputs).build();
    }
}
