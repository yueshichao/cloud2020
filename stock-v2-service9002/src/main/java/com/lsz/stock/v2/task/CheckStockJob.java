package com.lsz.stock.v2.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.lsz.stock.api.util.SkuRedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class CheckStockJob {

    @Async
    @Scheduled(fixedRate = 3000)
    public void checkStock() {
//        log.debug("校验库存...");
    }

    @Resource
    RedissonClient redissonClient;

    public static final int OFFSET = 0;
    public static final DateField UNIT = DateField.HOUR;
    public static final long ONE_SEC = 1000;


    @Async
//    @Scheduled(fixedRate = 3 * 3600 * 1000)
    @Scheduled(fixedRate = 10 * ONE_SEC)// test
    public void deleteSerial() {
        Date minDate = DateUtil.date().offset(UNIT, OFFSET).toJdkDate();
        log.info("清理{}{}（{}）的所有流水号...", OFFSET, UNIT, DateUtil.date(minDate).toString());
        // keys testKey*                            ×
        // scan 0 match testKey* count 100          √
        RKeys keys = redissonClient.getKeys();
        String pattern = SkuRedisKeyUtil.getAllStockSerialKeyPattern();
        Iterable<String> keysByPattern = keys.getKeysByPattern(pattern, 100);
        for (String key : keysByPattern) {
            Date date = SkuRedisKeyUtil.getDateByKey(key);
            log.info("scan key = {}, date = {}", key, DateUtil.date(date).toString());
            if (date.getTime() <= minDate.getTime()) {
                deleteAsync(key);
                log.warn("delete async key = {}, date = {}", key, DateUtil.date(date).toString());
            }
        }
    }

    private void deleteAsync(String key) {
        // del      key     ×
        // unlink   key     √
        RSet<Object> set = redissonClient.getSet(key);
        set.deleteAsync();
    }

}
