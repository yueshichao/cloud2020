package com.atguigu.springcloud.alibaba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@ConditionalOnBean(KafkaProperties.Producer.class)
@Component
public class LogFilter implements Filter {

    @Value("${application.name}")
    private String appName;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        long delta = System.currentTimeMillis() - start;

        // 上报接口调用信息
        log.debug("appName = {}, url = {}, ip = {}", appName, servletRequest.toString(), servletRequest.getLocalAddr());
    }

    @Override
    public void destroy() {

    }
}
