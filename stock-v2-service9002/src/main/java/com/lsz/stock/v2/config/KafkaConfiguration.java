package com.lsz.stock.v2.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    public static final String SKU_STOCK_TOPIC = "SKU_STOCK_TOPIC";

    @Bean
    public NewTopic initialTopic() {
        return new NewTopic(SKU_STOCK_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic updateTopic() {
        return new NewTopic(SKU_STOCK_TOPIC, 1, (short) 1);
    }


}
