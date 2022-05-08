package com.lsz.stock.api.en;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum StockMsgEnum {

    REDUCE_SUCCESS(1, "扣减库存成功"),

    UNKNOWN(-1, "未知"),
    NOT_EXISTS(-2, "sku不存在"),
    REDUCE_LACK(-3, "库存不足"),
    REDUCE_REPEAT_ERR(-4, "库存流水重复"),
    REDUCE_FAIL(-5, "库存扣减失败"),
    ROLLBACK(-6, "回滚sku"),
    OFF(-7, "动作：下架sku"),

    ;

    int code;
    String desc;

    StockMsgEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static StockMsgEnum getEnumByCode(Integer code) {
        StockMsgEnum[] values = values();
        for (StockMsgEnum value : values) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return UNKNOWN;
    }

}
