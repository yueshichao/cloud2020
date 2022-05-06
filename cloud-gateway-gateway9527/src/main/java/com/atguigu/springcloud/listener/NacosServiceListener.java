package com.atguigu.springcloud.listener;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class NacosServiceListener {

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @PostConstruct
    public void init() throws Exception {

        //初始化监听服务上下线

        Properties properties = System.getProperties();
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);

        NamingService namingService = NamingFactory.createNamingService(properties);
        List<ServiceInfo> subscribeServices = namingService.getSubscribeServices();
        log.info("subscribeServices = {}", subscribeServices);

    }


}
