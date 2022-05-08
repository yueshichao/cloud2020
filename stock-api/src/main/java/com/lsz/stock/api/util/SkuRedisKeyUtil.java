package com.lsz.stock.api.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;

import java.util.Date;

public class SkuRedisKeyUtil {

    public static String getStockKey(Long skuId) {
        return StrFormatter.format("sku:stock:{}", skuId);
    }


    private static final String PRE_SERIAL_KEY = "sku:serial:";
    private static final String DATE_FORMAT = "yyyy-MM-dd-HH";


    public static String getStockSerialKey(Long skuId, Date operateTime) {
        // 按小时分桶，定期清理
        String bucket = DateUtil.format(operateTime, DATE_FORMAT);
        return StrFormatter.format(PRE_SERIAL_KEY + "{}:{}", bucket, skuId);
    }

    public static String getAllStockSerialKeyPattern() {
        return StrFormatter.format(PRE_SERIAL_KEY + "{}", "*");
    }

    public static Date getDateByKey(String key) {
        String replace = StrUtil.replace(key, PRE_SERIAL_KEY, "");
        String[] split = StrUtil.split(replace, ":");
        String bucketTime = split[0];
        return DateUtil.parse(bucketTime, DATE_FORMAT).toJdkDate();
    }

}
