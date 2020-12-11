package com.java_camp.distributed_transaction_at.sql_reverse;

// 生成逆向SQL接口
public interface SqlReverse {
    public String getReverseSql(String sql, String primaryKey);
}
