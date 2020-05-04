package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @ApiOperation("创建订单")
    @PostMapping(value = "/payment/create")
    public CommonResult<Integer> create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);
        log.info("*****插入结果：{}", result);
        return result > 0 ? new CommonResult<>(200, "插入数据库成功", result) : new CommonResult<>(444, "插入数据库失败");
    }

    @ApiOperation("获取订单")
    @GetMapping(value = "/payment/get{id}")
    public CommonResult<Payment> getPaymentId(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);

        return payment != null ?
                new CommonResult<>(200, "查询成功~", payment)
                :
                new CommonResult<>(444, "没有对应记录，查询失败");
    }


}
