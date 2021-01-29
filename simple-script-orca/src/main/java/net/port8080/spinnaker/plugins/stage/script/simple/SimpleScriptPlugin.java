package net.port8080.spinnaker.plugins.stage.script.simple;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleScriptPlugin extends Plugin {
    public SimpleScriptPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    private final Logger logger = LoggerFactory.getLogger(SimpleScriptPlugin.class);

    @Override
    public void start() {
        logger.info("SimpleScriptPlugin.start()");
    }

    @Override
    public void stop() {
        logger.info("SimpleScriptPlugin.stop()");
    }
}