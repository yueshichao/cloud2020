package com.lsz.stock.v2;

import com.lsz.stock.v2.task.CheckStockJob;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StockV2Main9002.class})
public class JobTest {

    @Resource
    CheckStockJob checkStockJob;

    @Test
    public void daoTest() {
        checkStockJob.deleteSerial();

    }

}
