package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import cz.cuni.mff.d3s.distrace.utils.InstrumentorConfFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

public class Instrumentor {
    private static Logger log;

    public Instrumentor addTransformer(String className, BaseTransformer transformer){
        TransformersManager.register(className, transformer);
        return this;
    }

    /**
     * This method has to be called in a custom implementation of Instrumentor in order to start the Instrumentor.
     * Usually before this method is called the programmer should register all classes which should be instrumented
     * using TransformerManager
     * @param args command line arguments of the instrumentor
     */
    public void start(String[] args){
        assert args.length == 4; // we always start Instrumentor from native agent and 4 parameters should be
        // always passed to it
        // - socket address
        // - log level
        // - log dir
        // - monitored application classpath. It is needed in order to resolve dependencies on class being instrumented

        String socketAddress = args[0];
        String logLevel = args[1];
        String logDir = args[2];
        String classPath = args[3];
        ConfigurationFactory.setConfigurationFactory(new InstrumentorConfFactory(logLevel, logDir));
        log = LogManager.getLogger(Instrumentor.class);
        log.info("Running forked JVM");

        InstrumentorServer server = new InstrumentorServer(socketAddress, classPath);
        server.start();
    }


}