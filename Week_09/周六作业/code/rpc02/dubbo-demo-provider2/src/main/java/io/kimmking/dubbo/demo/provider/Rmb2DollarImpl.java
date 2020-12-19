package io.kimmking.dubbo.demo.provider;

import io.kimmking.dubbo.demo.api.Rmb2Dollar;
import io.kimmking.dubbo.demo.core.foreign_exchange.ForeignExchange;
import io.kimmking.dubbo.demo.core.foreign_exchange.ForeignExchangeMySqlImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;

@DubboService(version = "1.0.0")
public class Rmb2DollarImpl implements Rmb2Dollar {
    private ForeignExchange fe;

    public Rmb2DollarImpl() {
        this.fe = new ForeignExchangeMySqlImpl("jdbc:mysql://192.168.3.146/java_camp?useServerPrepStmts=true", "root", "123456");
    }

    // 冻结人民币，Try步骤
    @Override
    @HmilyTCC(confirmMethod = "subAccountRmb", cancelMethod = "recoverHoldRmb")
    public Boolean transfer(String acctId, Long amount) {


        int a = 10 / 0;

        if (!fe.holdRmbByAccount(acctId, amount)) {
            System.err.println("Try fail");
            throw new RuntimeException("try fail, " + acctId + " " + amount);
        }

        return Boolean.TRUE;
    }

    // 同时减少冻结人民币数额和账户中的人民币数额，增加美元数额, confirm步骤
    public Boolean subAccountRmb(String acctId, Long amount) {
        if (!fe.subRmbByAccount(acctId, amount)) {
            System.err.println("Confirm fail");
            throw new RuntimeException("confirm fail, " + acctId + " " + amount);
        }

        return Boolean.TRUE;
    }

    // 恢复冻结的人民币，cancel步骤
    public Boolean recoverHoldRmb(String acctId, Long amount) {
        if (!fe.recoverHoldRmbByAccount(acctId, amount)) {
            System.err.println("Cancel fail");
            throw new RuntimeException("cancel fail, " + acctId + " " + amount);
        }

        return Boolean.TRUE;
    }
}
