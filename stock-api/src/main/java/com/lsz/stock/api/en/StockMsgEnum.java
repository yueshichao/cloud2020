package com.lsz.stock.api.en;

import lombok.Getter;

@Getter
public enum StockMsgEnum {

    UNKNOWN(1, "未知"),
    REDUCE_SUCCESS(1, "扣减库存成功"),
    REDUCE_FAIL(1, "库存扣减失败"),
    REDUCE_LACK(1, "库存不足"),
    NOT_EXISTS(-1, "sku不存在"),
    ROLLBACK(1, "回滚sku"),
    OFF(1, "下架sku"),

    ;

    int code;
    String desc;

    StockMsgEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
