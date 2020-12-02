package com.atguigu.shardingjdbcdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user")
public class User {
    private Long userId;
    private String username;
    private String ustatus;
}
