package com.lsz.stock.v2.serial.consumer;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lsz.stock.api.dto.StockMsgDTO;
import com.lsz.stock.api.en.StockMsgEnum;
import com.lsz.stock.api.po.StockPO;
import com.lsz.stock.api.po.StockSerialPO;
import com.lsz.stock.v2.serial.converter.StockConverter;
import com.lsz.stock.v2.serial.dao.StockDao;
import com.lsz.stock.v2.serial.dao.StockSerialDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.lsz.stock.api.util.SkuRedisKeyUtil.getStockKey;
import static com.lsz.stock.v2.serial.config.KafkaConfiguration.SKU_STOCK_TOPIC;

@Component
@Slf4j
public class StockSerialConsumer {

    @Resource
    StockDao stockDao;
    @Resource
    StockSerialDao stockSerialDao;

    @Resource
    RedissonClient redissonClient;

    public static String STOCK_ROLLBACK_SCRIPT = "";

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("lua/stock_rollback.lua");
        STOCK_ROLLBACK_SCRIPT = FileUtil.readString(classPathResource.getFile(), Charset.forName("utf-8"));
        STOCK_ROLLBACK_SCRIPT = STOCK_ROLLBACK_SCRIPT.replaceAll("\r", "");
        log.info("STOCK_ROLLBACK_SCRIPT = {}", STOCK_ROLLBACK_SCRIPT);
    }

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
        rollbackRedis(stockMsgDTO);
        // TODO mysql补偿
        StockSerialPO stockSerialPO = StockConverter.convert2StockSerialPO(stockMsgDTO);
        stockSerialDao.insert(stockSerialPO);
        stockDao.incr(stockSerialPO.getSkuId(), Long.valueOf(stockSerialPO.getStockDelta()));
    }

    private void rollbackRedis(StockMsgDTO stockMsgDTO) {
        Long skuId = stockMsgDTO.getSkuId();
        Long rollbackStock = stockMsgDTO.getNum();
        String serialId = stockMsgDTO.getSerialId();
        Object result = redissonClient.getScript(new StringCodec()).eval(RScript.Mode.READ_WRITE,
                STOCK_ROLLBACK_SCRIPT,
                RScript.ReturnType.VALUE,
                Lists.newArrayList(getStockKey(skuId)),
                rollbackStock, serialId
        );

        if (result != null) {
            log.debug("clazz = {}, result = {}", result.getClass(), result);
        } else {
            log.debug("result = {}", result);
        }


    }

    @Transactional
    public void handleDecr(StockMsgDTO stockMsgDTO) {
        Long skuId = stockMsgDTO.getSkuId();
        stockDao.decr(skuId, stockMsgDTO.getNum());
        StockSerialPO stockSerialPO = StockConverter.convert2StockSerialPO(stockMsgDTO);
        stockSerialDao.insert(stockSerialPO);
    }

    private void handleNotExists(StockMsgDTO stockMsgDTO) {
        Long skuId = stockMsgDTO.getSkuId();
        // 判断并设置，要原子操作
        // 库存不存在，刷新库存
        RBucket<String> bucket = redissonClient.getBucket(getStockKey(skuId), new StringCodec());
        if (bucket.get() == null) {
            StockPO stockPO = stockDao.selectOne(skuId);
            // redis: set stock 9999
            bucket.trySet(String.valueOf(stockPO.getStock()));
        }

    }

}
