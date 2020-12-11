package com.java_camp.distributed_transaction_at.application;


import com.java_camp.distributed_transaction_at.transactin_manager.SqlPack;
import com.java_camp.distributed_transaction_at.transactin_manager.TransactionManager;
import lombok.extern.java.Log;


@Log
public class ApplicationInsertData implements Application {
    @Override
    public void processBusiness(TransactionManager tm) {
        SqlPack[] sqls1 = new SqlPack[] {
                new SqlPack("insert into t_id_name (id, name) values (100, 'XiaoMing100')", "id"),
                new SqlPack("insert into t_id_name (id, name) values (200, 'XiaoHong200')", "id")
        };

        SqlPack[] sqls2 = new SqlPack[] {
                new SqlPack("insert into t_id_name (id, name) values (1000, 'XiaoMing1000')", "id"),
                new SqlPack("insert into t_id_name (id, name) values (2000, 'XiaoHong2000')", "id")
        };

        // 添加分支事务, 全局事务号为100
        if (!tm.addBranchTransaction(100, "ds1", sqls1)) {
            log.info("add branch transaction fail");
            return;
        }

        if (!tm.addBranchTransaction(100, "ds2", sqls2)) {
            log.info("add branch transaction fail");
            return;
        }

        if (!tm.runGlobalTransaction(100)) {
            log.info("run global transaction fail");
        } else {
            log.info("run global transaction ok");
        }
    }
}
