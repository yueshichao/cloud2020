package com.lsz.stock.v2.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckStockJob {

    @Async
    @Scheduled(fixedRate = 3000)
    public void checkStock() {
        log.debug("校验库存...");
    }

}
