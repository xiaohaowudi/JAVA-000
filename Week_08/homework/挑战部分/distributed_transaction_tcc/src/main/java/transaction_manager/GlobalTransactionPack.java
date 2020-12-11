package transaction_manager;

import com.java_camp.distributed_transaction_tcc.application.TccExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import resource_manager.ResourceManager;

import java.util.List;

// 全局事务抽象
@Data
@AllArgsConstructor
public class GlobalTransactionPack {
    private TccExecutor executor;       // 执行事务的业务层对象
    List<ResourceManager> rms;          // 全局事务包含的要操作的数据源
}
