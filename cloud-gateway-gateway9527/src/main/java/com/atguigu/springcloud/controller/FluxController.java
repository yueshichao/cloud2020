package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.service.IdGenerator;
import com.atguigu.springcloud.trace.MyTracer;
import com.atguigu.springcloud.trace.MyTracerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController
@RequestMapping("/gate")
@Slf4j
@ConditionalOnBean({KafkaProperties.Producer.class})
public class FluxController {

    @Resource(name = "idGenerator")
    IdGenerator idGenerator;

    @Resource
    WebClient webClient;

    @Resource(name = "threadPool")
    ThreadPoolTaskExecutor threadPool;

    @RequestMapping("/get/id")
    public Mono<Long> getId() {

        return Mono.just(idGenerator.getId());
    }

    @RequestMapping("/proxy/{app}/{uri}")
    public Mono<String> proxyDemo(@PathVariable String app, @PathVariable String uri) {
        uri = uri.replace("_", "/");
        return doProxyDemo(app, uri);
    }

    private Mono<String> doProxyDemo(String app, String uri) {
        log.info("proxy app = {}, uri = {}", app, uri);
        WebClient.RequestBodySpec req = webClient.method(HttpMethod.GET)
                .uri("http://127.0.0.1:3377/" + uri);
        Mono<ClientResponse> responseMono = req.exchange();
        return responseMono.flatMap((resp) -> {
            return resp.bodyToMono(String.class);
        });
    }

    @RequestMapping("/trace")
    public Mono<Void> trace() {
        MyTracer tracer = MyTracerContext.createIfAbsent();
        try {
            log.info("main trace = {}", tracer);
            threadPool.execute(() -> {
                MyTracer subTracer = MyTracerContext.createIfAbsent();
                log.info("executor trace = {}", subTracer);
            });
        } finally {
            MyTracerContext.remove();
        }
        return Mono.empty();
    }


}
