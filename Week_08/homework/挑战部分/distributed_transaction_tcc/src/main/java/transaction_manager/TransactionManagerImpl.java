package transaction_manager;

import com.java_camp.distributed_transaction_tcc.application.TccExecutor;
import lombok.extern.java.Log;
import resource_manager.ResourceManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TM 实现类
@Log
public class TransactionManagerImpl implements TransactionManager {

    private final ConcurrentHashMap<Long, GlobalTransactionPack> xid2trans;          // 事务id到全局事务的映射
    private final ConcurrentHashMap<String, ResourceManager> name2rm;               // 数据源名称到数据源的映射

    public TransactionManagerImpl(List<ResourceManager> rms) {
        xid2trans = new ConcurrentHashMap<Long, GlobalTransactionPack>();
        name2rm = new  ConcurrentHashMap<String, ResourceManager>();

        for (ResourceManager rm : rms) {
            name2rm.put(rm.getRmName(), rm);
        }
    }

    @Override
    public boolean addBranchTransactionExecutor(long xid, List<String> dataSourceNames, TccExecutor executor) {
        List<ResourceManager> rms = new LinkedList<>();
        for (String dataSourceName : dataSourceNames) {
            if (!name2rm.containsKey(dataSourceName)) {
                log.info("invalid data source name " + dataSourceName);
                return false;
            }
            rms.add(name2rm.get(dataSourceName));
        }

        synchronized (this) {
            if (xid2trans.containsKey(xid)) {
                log.info("global transaction already exist, xid = " + xid);
                return false;
            }

            xid2trans.put(xid, new GlobalTransactionPack(executor, rms));
        }

        return true;
    }


    @Override
    public boolean runGlobalTransaction(long xid) {

        if (!xid2trans.containsKey(xid)) {
            log.info("invalid transaction, xid = " + xid);
            return false;
        }

        boolean isSuccess = true;
        Set<ResourceManager> visitedRM = new HashSet<>();      // 已经执行过try操作的数据源

        // 2PC 第一阶段，所有分支事务执行Try
        GlobalTransactionPack pack = xid2trans.get(xid);
        for (ResourceManager rm : pack.getRms()) {
            if (!pack.getExecutor().tccTry(xid, rm)) {
                isSuccess = false;
                break;
            }

            visitedRM.add(rm);
        }

        // 2PC 第二阶段，根据Try阶段结果执行commit或者cancel
        if (isSuccess) {
            for (ResourceManager rm : visitedRM) {
                if (!pack.getExecutor().tccCommit(xid, rm)) {
                    isSuccess = false;
                    log.info("commit branch transaction fail, xid = " + xid + ", data source = " + rm.getRmName());
                } else {
                    log.info("commit branch transaction ok, xid = " + xid + ", data source = " + rm.getRmName());
                }
            }
        } else {
            for (ResourceManager rm : visitedRM) {
                if (!pack.getExecutor().tccCancel(xid, rm)) {
                    log.info("cancel branch transaction fail, xid = " + xid + ", data source = " + rm.getRmName());
                } else {
                    log.info("cancel branch transaction ok, xid = " + xid + ", data source = " + rm.getRmName());
                }
            }
        }

        return isSuccess;
    }
}
