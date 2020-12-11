package com.java_camp.distributed_transaction_at;

import com.java_camp.distributed_transaction_at.application.Application;
import com.java_camp.distributed_transaction_at.application.ApplicationInsertData;
import com.java_camp.distributed_transaction_at.application.ApplicationInsertDataFail;
import com.java_camp.distributed_transaction_at.resource_manager.MySqlRM;
import com.java_camp.distributed_transaction_at.resource_manager.ResourceManager;
import com.java_camp.distributed_transaction_at.transactin_manager.TransactionManager;
import com.java_camp.distributed_transaction_at.transactin_manager.TransactionManagerAtImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class DistributedTransactionAtApplicationTests {

    // 测试分布式事务执行成功场景
    @Test
    void testTransactionSuccess() {
        // 先构造TM， RM的模拟环境
        ResourceManager rm1 = new MySqlRM("ds1", "jdbc:mysql://192.168.3.144/java_camp?useServerPrepStmts=true", "root", "123456");
        ResourceManager rm2 = new MySqlRM("ds2", "jdbc:mysql://192.168.3.146/java_camp?useServerPrepStmts=true", "root", "123456");

        List<ResourceManager> rms = new LinkedList<ResourceManager>();
        rms.add(rm1);
        rms.add(rm2);
        TransactionManager tm = new TransactionManagerAtImpl(rms);

        Application app = new ApplicationInsertData();
        app.processBusiness(tm);
    }

    // 测试分布式事务回滚场景
    @Test
    void testTransactionRollback() {
        // 先构造TM， RM的模拟环境
        ResourceManager rm1 = new MySqlRM("ds1", "jdbc:mysql://192.168.3.144/java_camp?useServerPrepStmts=true", "root", "123456");
        ResourceManager rm2 = new MySqlRM("ds2", "jdbc:mysql://192.168.3.146/java_camp?useServerPrepStmts=true", "root", "123456");

        List<ResourceManager> rms = new LinkedList<ResourceManager>();
        rms.add(rm1);
        rms.add(rm2);
        TransactionManager tm = new TransactionManagerAtImpl(rms);

        Application app = new ApplicationInsertDataFail();
        app.processBusiness(tm);
    }


}
