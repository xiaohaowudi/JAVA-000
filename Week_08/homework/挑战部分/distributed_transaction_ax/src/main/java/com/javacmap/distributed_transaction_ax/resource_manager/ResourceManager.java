package com.javacmap.distributed_transaction_ax.resource_manager;

import java.util.List;

// 本地资源管理器接口
public interface ResourceManager {
    // 获取RM名称接口
    public String getRmName();

    // 一阶段 xa prepare 接口, 执行全局事务号是xid的事务
    public boolean xaPrepare(long xid, List<String> sqls);

    // 二阶段提交事务接口
    public boolean xaCommit(long xid);

    // 二阶段回滚事务接口
    public boolean xaRollback(long xid);
}
