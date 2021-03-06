package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.api.TraceContext;
import net.bytebuddy.asm.Advice.BoxedReturn;
import net.bytebuddy.asm.Advice.OnMethodExit;

import java.lang.reflect.Field;


public class CallbackCreatorAdvice {

    @OnMethodExit
    public static Object exit(@BoxedReturn Object value) {
        try {
            Field f = value.getClass().getDeclaredField("traceContext");
            f.setAccessible(true);
            f.set(value, new TraceContext());
            TraceContext context = (TraceContext) f.get(value);
            System.out.println("Created callback with trace ID = " +context.getTraceId());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return value;
    }
}
