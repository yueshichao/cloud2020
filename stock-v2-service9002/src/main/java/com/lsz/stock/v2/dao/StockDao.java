package com.lsz.stock.v2.dao;

import com.lsz.stock.v2.po.StockPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockDao {

    int insert(StockPO stockPO);

    int decr(@Param("skuId") Long skuId);

}
