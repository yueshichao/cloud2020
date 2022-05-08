package com.lsz.stock.v2.serial.dao;

import com.lsz.stock.api.po.StockSerialPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockSerialDao {

    int insert(StockSerialPO stockPO);

}
