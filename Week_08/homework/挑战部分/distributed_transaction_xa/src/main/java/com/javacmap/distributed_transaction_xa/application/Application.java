package com.javacmap.distributed_transaction_xa.application;

import com.javacmap.distributed_transaction_xa.transaction_manager.TransactionManager;

public interface Application {
    // 上游业务执行接口
    public void processBusiness(TransactionManager tm);
}
