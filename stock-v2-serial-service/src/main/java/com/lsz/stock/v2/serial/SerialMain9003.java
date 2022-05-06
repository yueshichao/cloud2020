package com.lsz.stock.v2.serial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SerialMain9003 {
    public static void main(String[] args) {
        SpringApplication.run(SerialMain9003.class, args);
    }
}
