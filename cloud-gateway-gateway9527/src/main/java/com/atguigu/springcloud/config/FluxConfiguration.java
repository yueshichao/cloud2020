package com.atguigu.springcloud.config;

import com.atguigu.springcloud.service.IdGenerator;
import com.atguigu.springcloud.service.TraceGenerator;
import com.atguigu.springcloud.trace.MyTraceThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

@Configuration
public class FluxConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean("threadPool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setMaxPoolSize(1);
        return new MyTraceThreadPoolTaskExecutor(delegate);
//        return delegate;
    }

    @Bean
    public TraceGenerator traceGenerator() {
        TraceGenerator traceGenerator = new TraceGenerator() {
            @Override
            public String traceId() {
                return UUID.randomUUID().toString().replace("-", "");
            }
        };
        return traceGenerator;
    }

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator() {
            LongAdder idGenerator = new LongAdder();

            @Override
            public long getId() {
                idGenerator.increment();
                return idGenerator.longValue();
            }
        };
    }

}
