package com.lsz.stock.v2.serial;

import com.lsz.stock.api.po.StockSerialPO;
import com.lsz.stock.v2.serial.dao.StockSerialDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SerialMain9003.class})
public class DaoTest {

    @Resource
    StockSerialDao stockSerialDao;

    @Test
    public void daoTest() {
        StockSerialPO stockSerialPO = new StockSerialPO();
        stockSerialPO.setId(0L);
        stockSerialPO.setSerialId("");
        stockSerialPO.setType(0);
        stockSerialPO.setSkuId(0L);
        stockSerialPO.setStockDelta(0);
        stockSerialDao.insert(stockSerialPO);

    }

}
