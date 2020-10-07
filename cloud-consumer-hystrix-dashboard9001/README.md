# 1. 客户端配置如下：
```java
    /**
     * 此配置是为了服务监控而配置，与服务容错本身无关，spring-cloud升级后的坑
     * 因为spring boot默认路径不是"hystrix.stream"
     */
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricStreamServlet");
        return registrationBean;
    }

```
> 客户端pom里一定要有  
> 1. spring-boot-starter-actuator
> 2. spring-boot-starter-web

# 2. 访问`http://localhost:9001/hystrix/`

# 3. 输入客户端地址：`localhost:8001/hystrix.stream`