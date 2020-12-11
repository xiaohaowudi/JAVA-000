package com.java_camp.distributed_transaction_tcc.application;

import transaction_manager.TransactionManager;

public interface Application {
    public void processBusiness(TransactionManager tm);          // 业务执行接口
}
