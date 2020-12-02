package com.atguigu.shardingjdbcdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

// 订单基本信息
@Data
@TableName("OrderBaseInfo")
public class OrderBase {
    private Long orderId;
    private Long supplierUserId;
    private Long customerUserId;
    private Byte status;
    private String tradeNo;
    private Long createTimeStamp;
    private Long modifyTimeStamp;
}
