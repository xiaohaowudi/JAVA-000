package com.javacmap.distributed_transaction_ax.transaction_manager;


// 全局事务管理器接口
public interface TransactionManager {

    // 向数据源添加分支事务
    public boolean addBranchTransaction(long xid, String dataSourceName, String[] sqls);

    // 启动全局分布式事务
    public boolean runGlobalTransaction(long xid);
}
