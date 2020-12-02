package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
