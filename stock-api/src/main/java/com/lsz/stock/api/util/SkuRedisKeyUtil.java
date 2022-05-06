package com.lsz.stock.api.util;

import cn.hutool.core.text.StrFormatter;

public class SkuRedisKeyUtil {

    public static String getStockKey(Long skuId) {
        return StrFormatter.format("sku:stock:{}", skuId);
    }

}
