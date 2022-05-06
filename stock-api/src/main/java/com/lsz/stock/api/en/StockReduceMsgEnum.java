package com.lsz.stock.api.en;

import lombok.Getter;

@Getter
public enum StockReduceMsgEnum {

    SUCCESS(1, "成功"),

    ;

    int code;
    String desc;

    StockReduceMsgEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
