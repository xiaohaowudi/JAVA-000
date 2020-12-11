package com.javacmap.distributed_transaction_xa.transaction_manager;

import com.javacmap.distributed_transaction_xa.resource_manager.ResourceManager;
import lombok.extern.java.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TM 实现类
@Log
public class TransactionManagerImpl implements TransactionManager {

    private final ConcurrentHashMap<Long, List<BranchTransPack>> xid2branchTrans;     // 事务id到分支事务执行的sql序列的映射
    private final ConcurrentHashMap<String, ResourceManager> name2rm;                 // 数据源名称到数据源的映射

    public TransactionManagerImpl(List<ResourceManager> rms) {
        xid2branchTrans = new ConcurrentHashMap<Long, List<BranchTransPack>>();
        name2rm = new  ConcurrentHashMap<String, ResourceManager>();

        for (ResourceManager rm : rms) {
            name2rm.put(rm.getRmName(), rm);
        }
    }

    @Override
    public boolean addBranchTransaction(long xid, String dataSourceName, String[] sqls) {
        if (!name2rm.containsKey(dataSourceName)) {
            return false;
        }

        synchronized (this) {
            if (!xid2branchTrans.containsKey(xid)) {
                xid2branchTrans.put(xid, new LinkedList<BranchTransPack>());
            }

            List<BranchTransPack> branchList = xid2branchTrans.get(xid);
            branchList.add(new BranchTransPack(dataSourceName, Arrays.asList(sqls)));
        }

        return true;
    }

    @Override
    public boolean runGlobalTransaction(long xid) {
        boolean isSuccess = true;
        Set<ResourceManager> visitedRM = new HashSet<>();      // 已经提交过prepare请求的数据源

        // 2PC 第一阶段，向所有RM发送请求
        List<BranchTransPack> branchTransPacks = xid2branchTrans.get(xid);
        for (BranchTransPack pack : branchTransPacks) {
            if (!name2rm.containsKey(pack.getDataSourceName())) {
                isSuccess = false;
                break;
            }

            if (!name2rm.get(pack.getDataSourceName()).xaPrepare(xid, pack.getSql())) {
                isSuccess = false;
                break;
            }

            visitedRM.add(name2rm.get(pack.getDataSourceName()));
        }

        // 2PC 第二阶段决策进行提交或者回滚
        if (isSuccess) {
            // 第一阶段prepare所有节点都成功，提交所有分支事务
            for (ResourceManager rm : visitedRM) {
                // 分支事务提交失败，记录日志，人工介入
                if (!rm.xaCommit(xid)) {
                    log.info("branch transaction commit fail!!! xid = " + xid + ", data source = " + rm.getRmName());
                    isSuccess = false;
                } else {
                    log.info("branch transaction commit ok xid = " + xid + ", data source = " + rm.getRmName());
                }
            }
        } else {
            // 第一阶段prepare至少有一个节点失败，已经提交过请求的RM全部进行回滚
            for (ResourceManager rm : visitedRM) {
                // 分支事务回滚失败，记录日志，人工介入
                if (!rm.xaRollback(xid)) {
                    log.info("branch transaction rollback fail!!! xid = " + xid + ", data source = " + rm.getRmName());
                } else {
                    log.info("branch transaction rollback ok xid = " + xid + ", data source = " + rm.getRmName());
                }
            }
        }

        return isSuccess;
    }
}
