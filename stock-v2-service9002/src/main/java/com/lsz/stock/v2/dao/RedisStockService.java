package com.lsz.stock.v2.dao;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.lsz.stock.api.en.StockMsgEnum;
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

import static com.lsz.stock.api.util.SkuRedisKeyUtil.getStockKey;

@Service
@Slf4j
public class RedisStockService {

    @Resource
    RedissonClient redissonClient;

    public static final StringCodec codec = new StringCodec();
    public static String stockScript = "";


    @PostConstruct
    public void init() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("lua/stock.lua");
        stockScript = FileUtil.readString(classPathResource.getFile(), Charset.forName("utf-8"));
        stockScript = stockScript.replaceAll("\r", "");
        log.info("stockScript = {}", stockScript);
    }


    public StockMsgEnum decr(Long skuId, Integer needStock, String serialId) {
        Object result = redissonClient.getScript(new StringCodec()).eval(RScript.Mode.READ_WRITE,
                stockScript,
                RScript.ReturnType.VALUE,
                Lists.newArrayList(getStockKey(skuId)),
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
        switch (code) {
            case -1:
                return StockMsgEnum.UNKNOWN;
            case -2:
                return StockMsgEnum.NOT_EXISTS;
            case -3:
                return StockMsgEnum.REDUCE_LACK;
            default:
                return StockMsgEnum.UNKNOWN;
        }
    }


}
