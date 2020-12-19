package io.kimmking.dubbo.demo.provider;

import io.kimmking.dubbo.demo.api.Dollar2Rmb;
import io.kimmking.dubbo.demo.api.Rmb2Dollar;

import io.kimmking.dubbo.demo.core.foreign_exchange.ForeignExchange;
import io.kimmking.dubbo.demo.core.foreign_exchange.ForeignExchangeMySqlImpl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;

@DubboService(version = "1.0.0")
public class Dollar2RmbImpl implements Dollar2Rmb {

    @DubboReference(version = "1.0.0")
    private Rmb2Dollar rmb2Dollar;

    private ForeignExchange fe;

    public Dollar2RmbImpl() {
        this.fe = new ForeignExchangeMySqlImpl("jdbc:mysql://192.168.3.144/java_camp?useServerPrepStmts=true", "root", "123456");
    }

    // 冻结美元，Try步骤
    @HmilyTCC(confirmMethod = "subAccountDollar", cancelMethod = "recoverHoldDollar")
    public Boolean transfer(String acctId1, Long amount1, String acctId2, Long amount2) {
        if (!fe.holdDollarByAccount(acctId1, amount1)) {
            throw new RuntimeException("try fail, " + acctId1 + " " + amount1);
        }


        Boolean ret = rmb2Dollar.transfer(acctId2, amount2);
        System.err.println("rmb2Dollar " + ret);

        return Boolean.TRUE;
    }

    // 同时减少冻结美元数额和账户中的美元数额，confirm步骤
    public Boolean subAccountDollar(String acctId1, Long amount1, String acctId2, Long amount2) {
        if (!fe.subDollarByAccount(acctId1, amount1)) {
            throw new RuntimeException("confirm fail, " + acctId1 + " " + amount1);
        }

        return Boolean.TRUE;
    }

    // 恢复冻结的美元，cancel步骤
    public Boolean recoverHoldDollar(String acctId1, Long amount1, String acctId2, Long amount2) {
        if (!fe.recoverHoldDollarByAccount(acctId1, amount1)) {
            throw new RuntimeException("cancel fail, " + acctId1 + " " + amount1);
        }

        return Boolean.TRUE;
    }

}
