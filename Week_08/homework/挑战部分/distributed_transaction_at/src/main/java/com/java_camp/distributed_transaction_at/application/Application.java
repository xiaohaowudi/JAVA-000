package com.java_camp.distributed_transaction_at.application;

import com.java_camp.distributed_transaction_at.transactin_manager.TransactionManager;

public interface Application {
    // 上游业务执行接口
    public void processBusiness(TransactionManager tm);
}
