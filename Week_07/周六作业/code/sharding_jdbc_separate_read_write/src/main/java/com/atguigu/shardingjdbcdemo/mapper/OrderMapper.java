package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.OrderBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends BaseMapper<OrderBase> {
}
