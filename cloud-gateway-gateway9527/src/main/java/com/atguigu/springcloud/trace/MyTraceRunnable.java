package com.atguigu.springcloud.trace;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyTraceRunnable implements Runnable {

    private final MyTracer tracer;

    private final Runnable runnable;

    @Override
    public void run() {
        MyTracerContext.set(tracer);
        try {
            runnable.run();
        } finally {
            MyTracerContext.remove();
        }
    }

}
