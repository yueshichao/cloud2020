package com.lsz.stock.v2.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import com.lsz.stock.v2.dao.StockDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/stock")
public class StockV1Controller {


    @Resource
    StockDao stockDao;

    @GetMapping("/v1/decr/{skuId}")
    public String decr(@PathVariable String skuId) {
        int affectRows = stockDao.decr(Long.parseLong(skuId));
        String result = affectRows > 0 ? "success" : "fail";
        log.info("decr result = {}", result);
        return StrFormatter.format("result = {}, date = {}", result, DateUtil.now());
    }

}
