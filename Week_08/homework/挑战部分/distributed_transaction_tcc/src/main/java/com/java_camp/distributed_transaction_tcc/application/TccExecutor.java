package com.java_camp.distributed_transaction_tcc.application;

import resource_manager.ResourceManager;

// tcc 三个操作的接口
public interface TccExecutor {
    public boolean tccTry(long xid, ResourceManager rm);         // 执行xid对应的全局事务中在rm上的分支事务
    public boolean tccCommit(long xid, ResourceManager rm);      // 提交事务
    public boolean tccCancel(long xid, ResourceManager rm);      // 反向恢复已经提交的分支事务, 手工补偿
}
