package com.javacmap.distributed_transaction_ax.application;

import com.javacmap.distributed_transaction_ax.transaction_manager.TransactionManager;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;


// 成功量像个数据源插入数据的app业务实现
@Log
public class ApplicationInsertData implements Application {
    @Override
    public void processBusiness(TransactionManager tm) {
        String[] sqls1 = new String[] {
            "insert into t_id_name (id, name) values (100, 'XiaoMing100')",
            "insert into t_id_name (id, name) values (200, 'XiaoHong200')",
        };

        String[] sqls2 = new String[] {
            "insert into t_id_name (id, name) values (1000, 'XiaoMing1000')",
            "insert into t_id_name (id, name) values (2000, 'XiaoHong2000')",
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
