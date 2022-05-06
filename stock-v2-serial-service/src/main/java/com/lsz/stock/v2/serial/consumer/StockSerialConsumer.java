package com.lsz.stock.v2.serial.consumer;

import com.alibaba.fastjson.JSON;
import com.lsz.stock.api.dto.StockMsgDTO;
import com.lsz.stock.api.en.StockMsgEnum;
import com.lsz.stock.api.po.StockPO;
import com.lsz.stock.v2.serial.dao.StockDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.lsz.stock.api.util.SkuRedisKeyUtil.getStockKey;
import static com.lsz.stock.v2.serial.config.KafkaConfiguration.SKU_STOCK_TOPIC;

@Component
@Slf4j
public class StockSerialConsumer {

    @Resource
    StockDao stockDao;

    @Resource
    RedissonClient redissonClient;

    @KafkaListener(topics = {SKU_STOCK_TOPIC})
    public void onMessage(ConsumerRecord<?, ?> record, Consumer consumer) {
        String value = (String) record.value();
        log.info("topic = {}, partition = {}, value = {}", record.topic(), record.partition(), value);
        StockMsgDTO stockMsgDTO = JSON.parseObject(value, StockMsgDTO.class);
        StockMsgEnum msgType = stockMsgDTO.getMsgType();
        switch (msgType) {
            case NOT_EXISTS:
                log.info("sku不存在");
                handleNotExists(stockMsgDTO);
                break;
            case REDUCE_SUCCESS:
                log.info("sku扣减");
                handleDecr(stockMsgDTO);
                break;
            case REDUCE_FAIL:
            case ROLLBACK:
                log.info("sku回滚");
                handleRollback(stockMsgDTO);
                break;
            case REDUCE_LACK:
                log.info("sku库存不足");
                break;
            default:
                break;
        }
        consumer.commitAsync();
    }

    private void handleRollback(StockMsgDTO stockMsgDTO) {
        // TODO redis补偿
        // TODO mysql补偿
    }

    private void handleDecr(StockMsgDTO stockMsgDTO) {
        Long skuId = stockMsgDTO.getSkuId();
        stockDao.decr(skuId, stockMsgDTO.getNum());
    }

    private void handleNotExists(StockMsgDTO stockMsgDTO) {
        Long skuId = stockMsgDTO.getSkuId();
        // 库存不存在，刷新库存
        RBucket<String> bucket = redissonClient.getBucket(getStockKey(skuId), new StringCodec());
        if (bucket.get() == null) {
            StockPO stockPO = stockDao.selectOne(skuId);
            // redis: set stock 9999
            bucket.set(String.valueOf(stockPO.getStock()));
        }

    }

}
