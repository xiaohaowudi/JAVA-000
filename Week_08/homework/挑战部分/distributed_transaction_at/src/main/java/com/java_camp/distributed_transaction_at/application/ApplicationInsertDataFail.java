package com.java_camp.distributed_transaction_at.application;

import com.java_camp.distributed_transaction_at.transactin_manager.SqlPack;
import com.java_camp.distributed_transaction_at.transactin_manager.TransactionManager;
import lombok.extern.java.Log;

@Log
public class ApplicationInsertDataFail implements Application {
    @Override
    public void processBusiness(TransactionManager tm) {
        SqlPack[] sqls1 = new SqlPack[] {
                new SqlPack("insert into t_id_name (id, name) values (300, 'XiaoMing100')", "id"),
                new SqlPack("insert into t_id_name (id, name) values (400, 'XiaoHong200')", "id")
        };

        SqlPack[] sqls2 = new SqlPack[] {
                new SqlPack("insert into t_id_name (id, name) values (5000, 'XiaoMing1000')", "id"),
                new SqlPack("insert into t_id_name (id, name) values (5000, 'XiaoHong2000')", "id")     // 故意构造主键重复错误，触发回滚
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
