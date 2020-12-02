package com.javacamp.SeparateReadWriteDemoSimple.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor
@Getter
@ToString
public class Order {
    private long orderId;
    private long sellerId;              // 卖方id
    private long customerId;            // 买方id
    private byte orderState;             // 订单状态
    private String tradeNo;             // 交易序列号
    private long createTimeStamp;       // 订单创建时间戳
    private long modifyTimeStamp;       // 订单基本信息变更事件戳
}
