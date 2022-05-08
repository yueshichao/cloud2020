package com.lsz.stock.api.po;

import com.lsz.stock.api.en.StockSerialEnum;
import lombok.Data;

/**
 * 库存流水表
 * 流水id，流水类型，skuId 共同构成唯一约束
 */
@Data
public class StockSerialPO {

    private Long id;
    private String serialId;
    /**
     * @see StockSerialEnum
     */
    private Integer type;
    private Long skuId;
    private Integer stockDelta;

}
