package com.atguigu.springcloud.trace;

import java.util.UUID;

public class MyTracerContext {

    public final static InheritableThreadLocal<MyTracer> threadLocal = new InheritableThreadLocal<>();

    public static MyTracer get() {
        return threadLocal.get();
    }

    public static MyTracer createIfAbsent() {
        MyTracer tracer = get();
        if (tracer == null) {
            tracer = new MyTracer();
            tracer.setTraceId(UUID.randomUUID().toString().replace("-", ""));
            set(tracer);
        }
        return tracer;
    }

    public static void set(MyTracer tracer) {
        threadLocal.set(tracer);
    }

    public static void remove() {
        threadLocal.remove();
    }

}
