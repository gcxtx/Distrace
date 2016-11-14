package cz.cuni.mff.d3s.distrace;

import com.rits.cloning.Cloner;
import com.rits.cloning.ObjenesisInstantiationStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Various helper methods
 */
public class Utils {
    private static final Logger log = LogManager.getLogger(InstrumentorServer.class);


    public static Map<String, byte[]> getInterceptorByteCodes(){
        // discover all interceptors

        Reflections reflections = new Reflections();
        Set<Class<? extends Interceptor>> classes = reflections.getSubTypesOf(Interceptor.class);

        Map<String, byte[]> byteCodes = new HashMap<>();
        log.info("Discovering interceptor classes");
        for(Class cls: classes){
            log.info("Found interceptor class: " + cls);
            InputStream inputStream = cls.getResourceAsStream("/"+cls.getName().replace(".","/")+".class");
            try {
                byteCodes.put(Utils.toNameWithSlashes(cls.getName()), toByteArray(inputStream));
            } catch (IOException ignore) {
               // this should never happen
            }
        }
        return byteCodes;
    }

    public static String toNameWithDots(String name){
        return name.replace('/', '.');
    }

    public static String toNameWithSlashes(String name){
        return name.replace('.', '/');
    }

    @SuppressWarnings("unused")
    public static void triggerLoading(String className, ClassLoader cl){
        //IDEA: We could create deep copy of class loader cl and load the class with that copy
        // this would ensure that we don't change the original class loading mechanisms
        // but we would be able to get the bytecode of the class
        Cloner cloner =  new Cloner(new ObjenesisInstantiationStrategy());
        ClassLoader clone = cloner.deepClone(cl);
        try {
           Class.forName(className.replace('/','.'), true, clone);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int reads = inputStream.read();
        while (reads != -1) {
            baos.write(reads);
            reads = inputStream.read();
        }
        return baos.toByteArray();
    }

    public static byte[] getBytesForClass(Class cl) throws IOException {
        String resourceName = "/" + cl.getName().replace(".","/") + ".class";
        InputStream inputStream = cl.getResourceAsStream(resourceName);
        return toByteArray(inputStream);
    }

}
