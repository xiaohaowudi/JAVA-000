package com.java_camp.distributed_transaction_tcc.application;

import lombok.extern.java.Log;
import resource_manager.ResourceManager;
import transaction_manager.TransactionManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// 成功向数据源插入数据的app业务实现
@Log
public class ApplicationInsertDataFail implements Application, TccExecutor {

    @Override
    public void processBusiness(TransactionManager tm) {
        List<String> dataSourceNames = new LinkedList<>();
        dataSourceNames.add("ds1");
        dataSourceNames.add("ds2");

        // 添加全局事务, 全局事务号为200
        if (!tm.addBranchTransactionExecutor(200, dataSourceNames, this)) {
            log.info("add transaction fail");
            return;
        }

        if (!tm.runGlobalTransaction(200)) {
            log.info("run global transaction fail");
        } else {
            log.info("run global transaction ok");
        }
    }

    @Override
    public boolean tccTry(long xid, ResourceManager rm) {
        boolean isSuccess = true;

        log.info("processing try xid = " + xid + ", data source = " + rm.getRmName());
        if (xid == 200) {
            if (rm.getRmName().equals("ds1")) {
                String[] sqls = new String[] {
                        "insert into t_id_name (id, name) values (300, 'XiaoMing100')",
                        "insert into t_id_name (id, name) values (400, 'XiaoHong200')",
                };

                isSuccess = rm.tccExecuteBranchTransaction(Arrays.asList(sqls));
            } else if (rm.getRmName().equals("ds2")) {
                String[] sqls = new String[] {
                        "insert into t_id_name (id, name) values (5000, 'XiaoMing1000')",
                        "insert into t_id_name (id, name) values (5000, 'XiaoHong2000')",   // 故意构造主键重复错误，触发回滚
                };

                isSuccess = rm.tccExecuteBranchTransaction(Arrays.asList(sqls));
            } else {
                log.info("unsupported xid " + xid);
                isSuccess = false;
            }
        }

        return isSuccess;
    }

    @Override
    public boolean tccCommit(long xid, ResourceManager rm) {
        // tcc模式下app在第二阶段commit什么都不用做，try阶段分支事务已经提交, 此处只记录日志作为测试用
        log.info("processing commit xid = " + xid + ", data source = " + rm.getRmName());
        return true;
    }

    @Override
    public boolean tccCancel(long xid, ResourceManager rm) {
        boolean isSuccess = true;

        log.info("processing cancel xid = " + xid + ", data source = " + rm.getRmName());
        if (xid == 200) {
            if (rm.getRmName().equals("ds1")) {
                String[] sqls = new String[] {
                        "delete from t_id_name where id = 300",
                        "delete from t_id_name where id = 400",
                };

                isSuccess = rm.tccExecuteBranchTransaction(Arrays.asList(sqls));
            } else if (rm.getRmName().equals("ds2")) {
                String[] sqls = new String[] {
                        "delete from t_id_name where id = 5000",
                };

                isSuccess = rm.tccExecuteBranchTransaction(Arrays.asList(sqls));
            } else {
                log.info("unsupported data source " + rm.getRmName());
                isSuccess = false;
            }
        } else {
            log.info("unsupported xid " + xid);
            isSuccess = false;
        }

        return isSuccess;
    }
}
