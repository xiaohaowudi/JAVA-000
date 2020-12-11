package com.javacmap.distributed_transaction_xa.application;

import com.javacmap.distributed_transaction_xa.transaction_manager.TransactionManager;
import lombok.extern.java.Log;

// 故意触发回滚的业务实现
@Log
public class ApplicationInsertDataFail implements Application {
    @Override
    public void processBusiness(TransactionManager tm) {
        String[] sqls1 = new String[] {
                "insert into t_id_name (id, name) values (300, 'XiaoMing100')",
                "insert into t_id_name (id, name) values (400, 'XiaoHong200')",
        };

        String[] sqls2 = new String[] {
                "insert into t_id_name (id, name) values (5000, 'XiaoMing1000')",
                "insert into t_id_name (id, name) values (5000, 'XiaoHong2000')",   // 故意构造主键重复错误，触发回滚
        };

        // 添加分支事务, 全局事务号为200
        if (!tm.addBranchTransaction(200, "ds1", sqls1)) {
            log.info("add branch transaction fail");
            return;
        }

        if (!tm.addBranchTransaction(200, "ds2", sqls2)) {
            log.info("add branch transaction fail");
            return;
        }

        if (!tm.runGlobalTransaction(200)) {
            log.info("run global transaction fail");
        } else {
            log.info("run global transaction ok");
        }
    }
}