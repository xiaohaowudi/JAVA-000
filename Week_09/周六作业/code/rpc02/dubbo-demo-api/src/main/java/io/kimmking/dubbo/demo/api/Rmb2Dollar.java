package io.kimmking.dubbo.demo.api;

import org.dromara.hmily.annotation.Hmily;

// 外汇人民币转美元
public interface Rmb2Dollar {
    @Hmily
    Boolean transfer(String acctId, Long amount);
}
