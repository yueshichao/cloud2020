package com.lsz.stock.v2.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
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

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
@RequestMapping("/stock")
public class StockV2Controller {

    @Resource
    RedisStockService redisStockService;
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;

    public static final AtomicLong repeatCallTimes = new AtomicLong(0);

    @PreDestroy
    public void destroy() {
        log.info("最终重复调用次数：{}", repeatCallTimes.get());
    }


    @GetMapping("/v2/decr/{skuId}")
    public String decr(@PathVariable("skuId") String skuIdStr) {
        // 订单流水，幂等id
        String serialId = getSerialId();
        // 订单创建时间，用于幂等ID分桶
        Date date = new Date();

        // 1/10的概率触发重复调用
        if (isRepeat()) {
            long times = repeatCallTimes.addAndGet(1);
            log.info("重复调用{}次！", times);
            doDecr(skuIdStr, serialId, date);
        }
        return doDecr(skuIdStr, serialId, date);
    }

    private String doDecr(String skuIdStr, String serialId, Date date) {
        if (DateUtil.between(DateUtil.date(), date, DateUnit.HOUR) > 3) {
            throw new RuntimeException("库存扣减失败，订单时间差距过长！");
        }
        long skuId = Long.parseLong(skuIdStr);

        // redis扣减库存
        int reduceStock = 1;
        StockMsgEnum result = redisStockService.decr(skuId, reduceStock, serialId, date);
        log.info("decr serialId = {}, result = {}", serialId, result);

        // 扣减异常
        if (result == StockMsgEnum.REDUCE_REPEAT_ERR) {
            return result.getDesc();
        }

        // kafka保证最终一致性
        StockMsgDTO stockMsgDTO = new StockMsgDTO()
                .setSkuId(skuId)
                .setNum((long) reduceStock)
                .setSerialId(serialId)
                .setOperateDate(date)
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

    private boolean isRepeat() {
        int i = RandomUtil.randomInt(0, 10);
//        return i >= 9;
        return true;
    }


    private String getSerialId() {
        return IdUtil.fastSimpleUUID();
    }

}
