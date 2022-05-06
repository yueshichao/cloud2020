package com.lsz.stock.v1.dao;

import com.lsz.stock.api.po.StockPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockDao {

    int insert(StockPO stockPO);

    int decr(@Param("skuId") Long skuId);

}
