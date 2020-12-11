package com.javacmap.distributed_transaction_xa.transaction_manager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// 针对一个数据源的sql分支事务封装
@Data
@AllArgsConstructor
public class BranchTransPack {
    private String dataSourceName;
    private List<String> sql;
}
