package io.kimmking.dubbo.demo.api;

import org.dromara.hmily.annotation.Hmily;

// 外汇美元转人民币
public interface Dollar2Rmb {
    // 用户1从美元转人民币，用户2从人民币转美元
    @Hmily
    Boolean transfer(String acctId1, Long amount1, String acctId2, Long amount2);
}
