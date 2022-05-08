package com.lsz.stock.api.dto;

import com.lsz.stock.api.en.StockMsgEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 消息场景：
 * 库存扣减
 * 库存回滚
 * 库存不存在
 * sku下架
 */
@Data
@Accessors(chain = true)
public class StockMsgDTO {

    private Long skuId;

    private StockMsgEnum msgType;

    private Long num;

    private String serialId;

    private Date operateDate;


}
