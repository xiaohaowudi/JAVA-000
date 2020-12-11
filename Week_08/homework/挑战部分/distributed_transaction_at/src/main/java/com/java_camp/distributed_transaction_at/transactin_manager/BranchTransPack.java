package com.java_camp.distributed_transaction_at.transactin_manager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// 针对一个数据源的sql分支事务封装
@Data
@AllArgsConstructor
public class BranchTransPack {
    private String dataSourceName;
    private List<String> sql;           // 正向执行事务时候的sql
    private List<String> revSql;        // 自动补偿时候使用的逆向sql
}
