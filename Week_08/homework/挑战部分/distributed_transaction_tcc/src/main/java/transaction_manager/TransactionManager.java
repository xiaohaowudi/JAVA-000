package transaction_manager;


import com.java_camp.distributed_transaction_tcc.application.TccExecutor;

import java.util.List;

// 全局事务管理器接口
public interface TransactionManager {

    // 向数据源添加分支事务
    public boolean addBranchTransactionExecutor(long xid, List<String> dataSourceNames, TccExecutor executor);

    // 启动全局分布式事务
    public boolean runGlobalTransaction(long xid);
}
