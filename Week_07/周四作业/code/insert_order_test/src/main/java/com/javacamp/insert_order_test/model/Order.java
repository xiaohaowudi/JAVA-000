package com.javacamp.insert_order_test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class Order {
    private long sellerId;              // 卖方id
    private long customerId;            // 买方id
    private byte orderState;             // 订单状态
    private String tradeNo;             // 交易序列号
    private long createTimeStamp;       // 订单创建时间戳
    private long modifyTimeStamp;       // 订单基本信息变更事件戳
}
