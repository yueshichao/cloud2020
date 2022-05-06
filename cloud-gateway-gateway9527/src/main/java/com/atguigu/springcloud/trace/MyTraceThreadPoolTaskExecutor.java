package com.atguigu.springcloud.trace;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


public class MyTraceThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private ThreadPoolTaskExecutor delegate;

    public MyTraceThreadPoolTaskExecutor(ThreadPoolTaskExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable task) {
        delegate.execute(new MyTraceRunnable(MyTracerContext.get(), task));
    }

    @Override
    public void initialize() {
        delegate.initialize();
    }

}
