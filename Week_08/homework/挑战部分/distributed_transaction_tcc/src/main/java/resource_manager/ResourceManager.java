package resource_manager;

import java.util.List;

// 本地资源管理器接口
public interface ResourceManager {
    // 获取RM名称接口
    public String getRmName();

    // 执行分支事务
    public boolean tccExecuteBranchTransaction(List<String> sqls);
}
