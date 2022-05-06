package com.lsz.stock.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StockV2Main9002 {

    public static void main(String[] args) {
        SpringApplication.run(StockV2Main9002.class, args);
    }

}
