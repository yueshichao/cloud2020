package com.lsz.stock.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StockV1Main9001 {

    public static void main(String[] args) {
        SpringApplication.run(StockV1Main9001.class, args);
    }

}
