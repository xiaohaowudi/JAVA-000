package com.atguigu.shardingjdbcdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_udict")
public class Udict {
    private Long dictid;
    private String ustatus;
    private String uvalue;
}
