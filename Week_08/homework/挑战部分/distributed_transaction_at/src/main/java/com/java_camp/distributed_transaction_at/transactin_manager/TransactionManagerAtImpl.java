package com.java_camp.distributed_transaction_at.transactin_manager;


import com.java_camp.distributed_transaction_at.resource_manager.ResourceManager;
import com.java_camp.distributed_transaction_at.sql_reverse.SqlReverse;
import com.java_camp.distributed_transaction_at.sql_reverse.SqlReverseInsert;

import lombok.extern.java.Log;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// AT自动补偿模式的TM实现类
@Log
public class TransactionManagerAtImpl implements TransactionManager {

    private final ConcurrentHashMap<Long, List<BranchTransPack>> xid2branchTrans;     // 事务id到分支事务执行的sql序列的映射
    private final ConcurrentHashMap<String, ResourceManager> name2rm;                 // 数据源名称到数据源的映射
    private final SqlReverse insertReverse;

    public TransactionManagerAtImpl(List<ResourceManager> rms) {
        xid2branchTrans = new ConcurrentHashMap<Long, List<BranchTransPack>>();
        name2rm = new  ConcurrentHashMap<String, ResourceManager>();

        for (ResourceManager rm : rms) {
            name2rm.put(rm.getRmName(), rm);
        }

        insertReverse = new SqlReverseInsert();
    }

    @Override
    public boolean addBranchTransaction(long xid, String dataSourceName, SqlPack[] sqlPacks) {
        if (!name2rm.containsKey(dataSourceName)) {
            return false;
        }

        synchronized (this) {
            if (!xid2branchTrans.containsKey(xid)) {
                xid2branchTrans.put(xid, new LinkedList<BranchTransPack>());
            }

            List<BranchTransPack> branchList = xid2branchTrans.get(xid);
            List<String> revSqlList = new LinkedList<>();
            List<String> sqlList = new LinkedList<>();

            for (SqlPack sqlPack : sqlPacks) {
                String revSql = insertReverse.getReverseSql(sqlPack.sql, sqlPack.primaryKey);
                if (revSql == null) {
                    log.info("generate reverse sql fail, " + sqlPack.sql);
                    return false;
                }

                revSqlList.add(revSql);
                sqlList.add(sqlPack.sql);
            }

            branchList.add(new BranchTransPack(dataSourceName, sqlList, revSqlList));
        }

        return true;
    }

    @Override
    public boolean runGlobalTransaction(long xid) {
        boolean isSuccess = true;
        Set<BranchTransPack> visitedPack = new HashSet<>();      // 已经提交过请求的数据源

        // 2PC 第一阶段，向所有RM发送请求
        List<BranchTransPack> branchTransPacks = xid2branchTrans.get(xid);
        for (BranchTransPack pack : branchTransPacks) {
            if (!name2rm.containsKey(pack.getDataSourceName())) {
                isSuccess = false;
                break;
            }


            if (!name2rm.get(pack.getDataSourceName()).atExecuteBranchTransaction(pack.getSql())) {
                log.info("process branch transaction fail, xid = " + xid + ", data source = " + pack.getDataSourceName());
                isSuccess = false;
                break;
            }
            log.info("process branch transaction ok, xid = " + xid + ", data source = " + pack.getDataSourceName());

            visitedPack.add(pack);
        }

        // 2PC 第二阶段决策进行提交或者回滚
        if (!isSuccess) {
            // 第一阶段prepare所有节点都成功，提交所有分支事务
            for (BranchTransPack pack : visitedPack) {
                // 分支事务自动补偿
                if (!name2rm.get(pack.getDataSourceName()).atExecuteBranchTransaction(pack.getRevSql())) {
                    log.info("branch transaction revert fail, xid = " + xid + ", data source = " + pack.getDataSourceName());
                } else {
                    log.info("branch transaction revert ok, xid = " + xid + ", data source = " + pack.getDataSourceName());
                }
            }
        }

        return isSuccess;
    }


}
