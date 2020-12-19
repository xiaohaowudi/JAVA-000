package io.kimmking.dubbo.demo.core.foreign_exchange;

public interface ForeignExchange {

    // 冻结美元账户固定数额
    boolean holdDollarByAccount(String acctId, Long amount);

    // 冻结人民币账户固定数额
    boolean holdRmbByAccount(String acctId, Long amount);

    // 恢复美元账户冻结固定数值
    boolean recoverHoldDollarByAccount(String acctId, Long amount);

    // 恢复人民币账户冻结固定数值
    boolean recoverHoldRmbByAccount(String acctId, Long amount);

    // 减少美元账户数额(账户数值和冻结数值均减少)
    boolean subDollarByAccount(String acctId, Long amount);

    // 减少人民币账户数额(账户数值和冻结数值均减少)
    boolean subRmbByAccount(String acctId, Long amount);

    // 增加美元账户数额
    boolean addDollarByAccount(String acctId, Long amount);

    // 增加人民币账户数额
    boolean addRmbByAccount(String acctId, Long amount);
}

