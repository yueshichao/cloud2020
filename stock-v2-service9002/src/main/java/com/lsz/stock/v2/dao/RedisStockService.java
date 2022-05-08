package com.lsz.stock.v2.dao;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.lsz.stock.api.en.StockMsgEnum;
import com.lsz.stock.api.util.SkuRedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import static com.lsz.stock.api.util.SkuRedisKeyUtil.getStockKey;
import static com.lsz.stock.api.util.SkuRedisKeyUtil.getStockSerialKey;

@Service
@Slf4j
public class RedisStockService {

    @Resource
    RedissonClient redissonClient;

    public static final StringCodec codec = new StringCodec();
    public static String STOCK_REDUCE_SCRIPT = "";


    @PostConstruct
    public void init() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("lua/stock_reduce.lua");
        STOCK_REDUCE_SCRIPT = FileUtil.readString(classPathResource.getFile(), Charset.forName("utf-8"));
        STOCK_REDUCE_SCRIPT = STOCK_REDUCE_SCRIPT.replaceAll("\r", "");
        log.info("STOCK_REDUCE_SCRIPT = {}", STOCK_REDUCE_SCRIPT);
    }


    public StockMsgEnum decr(Long skuId, Integer needStock, String serialId, Date operateTime) {
        // 库存扣减，记录流水
        Object result = redissonClient.getScript(new StringCodec()).eval(RScript.Mode.READ_WRITE,
                STOCK_REDUCE_SCRIPT,
                RScript.ReturnType.VALUE,
                Lists.newArrayList(getStockKey(skuId), getStockSerialKey(skuId, operateTime)),
                needStock, serialId
        );

        if (result != null) {
            log.debug("clazz = {}, result = {}", result.getClass(), result);
            Long res = (Long) result;
            if (res < 0) {
                return getStrByCode(res);
            } else {
                return StockMsgEnum.REDUCE_SUCCESS;
            }
        } else {
            log.debug("result = {}", result);
        }
        return StockMsgEnum.UNKNOWN;
    }

    private StockMsgEnum getStrByCode(Long res) {
        int code = res.intValue();
        return StockMsgEnum.getEnumByCode(code);
    }


}
