package net.port8080.spinnaker.plugins.stage.script.simple;

import com.google.common.collect.ImmutableMap;
import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import javax.annotation.Nonnull;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.netflix.spinnaker.orca.pipeline.model.StageContext;
import com.netflix.spinnaker.orca.pipeline.util.ContextParameterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import jdk.nashorn.api.scripting.JSObject;

@Component
@NonnullByDefault
public final class SimpleScriptTask implements Task {
    public static final String TASK_NAME = "runSimpleScript";

    private final Logger logger = LoggerFactory.getLogger(SimpleScriptTask.class);

    private final ContextParameterProcessor contextParameterProcessor;

    @Autowired
    public SimpleScriptTask(ContextParameterProcessor contextParameterProcessor) {
        this.contextParameterProcessor = contextParameterProcessor;
    }

    @Nonnull
    @Override
    public TaskResult execute(@Nonnull StageExecution stage) {
        SimpleScriptContext context = stage.mapTo(SimpleScriptContext.class);
        StageContext execution_context = contextParameterProcessor.buildExecutionContext(stage);

        //ExecutionStatus executionStatus = ExecutionStatus.SUCCEEDED;
        TaskResult taskResult;
        ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<String, Object>();

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript"); // move to context soon
            engine.put("execution_context", execution_context);
            engine.put("stage_context", context);
            Object evalOutput = engine.eval(context.getScript()); // FIXME: Figure out if lists can come out right
            //JSObject evalOutput = (JSObject) engine.eval(context.getScript());
            if(evalOutput != null) {
                builder.put("script_output", evalOutput);
            }
            ImmutableMap<String, Object> outputs = builder.build();
            taskResult = TaskResult.builder(ExecutionStatus.SUCCEEDED).outputs(outputs).build();
        } catch (Exception ex) {
            // FIXME: This should just rethrow the exception and let the above class handle gracefully
            //        Really, need to figure out the 'spinnaker task' way to handle exceptions here.
            logger.warn("SimpleScriptTask threw an exception: {}", ex.getMessage());
            //builder.put("exception", ex.getMessage());
            taskResult = TaskResult.builder(ExecutionStatus.TERMINAL).context("error", ex.getMessage()).build();
            //executionStatus = ExecutionStatus.TERMINAL;
        }

        //ImmutableMap<String, Object> outputs = builder.build();

        //return TaskResult.builder(executionStatus).outputs(outputs).build();
        return taskResult;
    }
}
