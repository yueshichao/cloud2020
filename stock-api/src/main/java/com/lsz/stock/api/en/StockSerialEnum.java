package com.lsz.stock.api.en;

import lombok.Getter;

@Getter
public enum StockSerialEnum {

    REDUCE(1, "库存正常扣减"),
    ROLLBACK(2, "库存回滚"),
    UNKNOWN(-1, "未知"),

    ;

    int code;
    String desc;

    StockSerialEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
