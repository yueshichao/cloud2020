package com.atguigu.springcloud.trace;

import lombok.Data;

@Data
public class MyTracer {

    private String traceId;
    private String spanId;
    private String parentSpanId;

}
