package water;

import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.asm.Advice;

import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContext;
import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContextFrom;


public abstract class MRTaskAdvices {

    public static class compute2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                // start span
                //InstrumentUtils.getOrCreateTraceContext(o)
                       // .openNestedSpan("MRTask local work")
                       // .add("ipPort", H2O.getIpPortString());
                //MRTask task = (MRTask) o;
                //System.out.println("Compute2 (Local work) was called on node: " + H2O.getIpPortString() + " trace ID "); // getTraceContext().getTraceId());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                // close span
               // InstrumentUtils.storeCurrentSpan();

                //InstrumentUtils.getTraceContext().storeCurrentSpan();
                //MRTask task = (MRTask) o;
                //System.out.println("OnCompletition ( local reducing) was called on node: " + H2O.getIpPortString() + " trace ID "); // + getTraceContext().getTraceId());
            }
        }
    }

    public static class doAll {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                InstrumentUtils.getOrCreateTraceContext(o)
                        .openNestedSpan(H2O.getIpPortString() + " : MR Task Main Span")
                        .add("ipPort", H2O.getIpPortString());

                System.out.println("doAll: Created Span with ID: " + InstrumentUtils.getCurrentSpan().getSpanId());
                System.out.println("doAll: Method was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext().getTraceId());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                System.out.println("doAll: Storing Span with ID: "+ InstrumentUtils.getCurrentSpan().getSpanId());
                InstrumentUtils.storeCurrentSpan();
            }
        }
    }

    public static class remote_compute {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o){
            if (o instanceof SumMRTask) {
                // don't open nested span. Just start a new span
              InstrumentUtils.getOrCreateTraceContext(o)
                      .openNestedSpan(H2O.getIpPortString() + " : Remote Compute");

                System.out.println("Remote compute: "+ getTraceContextFrom(o).getTraceId());
            }
        }
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return RPC ret){
            if (o instanceof SumMRTask) {
               // InstrumentUtils.getOrCreateTraceContext(o)
                 //       .openNestedSpan("Remote Compute Job")
                   //     .add("ipPort", H2O.getIpPortString());

                // store span associated with this request
                // start a new span here
                if(ret == null){
                    InstrumentUtils.getCurrentSpan().add("target", "local node");
                    System.out.println("No remote work");
                }else{
                    H2ONode node = ret._target;
                    InstrumentUtils.getCurrentSpan().add("target", ret._target.getIpPortString());
                    System.out.println("Computation planned on " + node.getIpPortString());
                    InstrumentUtils.getCurrentSpan().add("size of RPC", ret._dt.asBytes().length);
                }

                InstrumentUtils.getOrCreateTraceContext(o).storeCurrentSpan();

                System.out.println("Remote compute was called on node: " + H2O.getIpPortString() + " trace ID " +  getTraceContext().getTraceId());
            }
        }
    }

    public static class setupLocal0 {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o){
            if( o instanceof SumMRTask) {
                InstrumentUtils.getOrCreateTraceContext(o)
                        .openNestedSpan(H2O.getIpPortString() + " : Local setup and splitting");
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o){
            if( o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                InstrumentUtils.getOrCreateTraceContext(o).getCurrentSpan()
                        .add("left", task._nleft == null ? "local": task._nleft._target.getIpPortString())
                        .add("right", task._nrite == null ? "local": task._nrite._target.getIpPortString());

                InstrumentUtils.getTraceContext().storeCurrentSpan();
                System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext().getTraceId());
            }
        }
    }

}
