package com.lsz.stock.v2.serial.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setDatabase(0)
                .setAddress("redis://lsz.env.com:6379");
        return Redisson.create(config);
    }


}
