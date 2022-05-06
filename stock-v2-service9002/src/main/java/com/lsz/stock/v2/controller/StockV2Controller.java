package com.lsz.stock.v2.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.lsz.stock.api.dto.StockMsgDTO;
import com.lsz.stock.api.en.StockMsgEnum;
import com.lsz.stock.v2.config.KafkaConfiguration;
import com.lsz.stock.v2.dao.RedisStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/stock")
public class StockV2Controller {

    @Resource
    RedisStockService redisStockService;
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/v2/decr/{skuId}")
    public String decr(@PathVariable("skuId") String skuIdStr) {
        long skuId = Long.parseLong(skuIdStr);
        String serialId = getSerialId();

        // redis扣减库存
        StockMsgEnum result = redisStockService.decr(skuId, 1, serialId);
        log.info("decr result = {}", result);

        // kafka保证最终一致性
        StockMsgDTO stockMsgDTO = new StockMsgDTO()
                .setSkuId(skuId)
                .setNum(1L)
                .setSerialId(serialId)
                .setMsgType(result);
        ListenableFuture<SendResult<String, String>> sendFuture = kafkaTemplate.send(KafkaConfiguration.SKU_STOCK_TOPIC,
                JSON.toJSONString(stockMsgDTO));
        sendFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("producerRecord = {}", result.getProducerRecord());
                log.info("recordMetadata = {}", result.getRecordMetadata());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("{}", ex);
            }
        });

        return StrFormatter.format("result = {}, date = {}", result.getDesc(), DateUtil.now());
    }


    private String getSerialId() {
        return IdUtil.fastSimpleUUID();
    }

}
