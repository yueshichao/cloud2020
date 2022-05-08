package com.lsz.stock.v2.serial.converter;

import com.lsz.stock.api.dto.StockMsgDTO;
import com.lsz.stock.api.en.StockMsgEnum;
import com.lsz.stock.api.en.StockSerialEnum;
import com.lsz.stock.api.po.StockSerialPO;

import java.util.Objects;

public class StockConverter {
    public static StockSerialPO convert2StockSerialPO(StockMsgDTO stockMsgDTO) {
        StockSerialPO stockSerialPO = new StockSerialPO();
        StockMsgEnum msgType = stockMsgDTO.getMsgType();
        StockSerialEnum serialEnum;
        if (Objects.equals(msgType, StockMsgEnum.REDUCE_SUCCESS)) {
            serialEnum = StockSerialEnum.REDUCE;
        } else if (Objects.equals(msgType, StockMsgEnum.REDUCE_FAIL) || Objects.equals(msgType, StockMsgEnum.ROLLBACK)) {
            serialEnum = StockSerialEnum.ROLLBACK;
        } else {
            serialEnum = StockSerialEnum.UNKNOWN;
        }
        stockSerialPO.setType(serialEnum.getCode());
        stockSerialPO.setSerialId(stockMsgDTO.getSerialId());
        stockSerialPO.setSkuId(stockMsgDTO.getSkuId());
        stockSerialPO.setStockDelta(stockMsgDTO.getNum().intValue());
        return stockSerialPO;
    }
}
