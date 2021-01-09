package com.javacamp.distributedcounter.counter;


import lombok.val;

// 分布式计数器接口
public interface DistributedCounterTemplate {
    // 新建一个Counter
    public boolean createCounter(String name, long initVal);

    // 删除一个Counter
    public boolean deleteCounter(String name);

    // 增加计数
    public boolean incrBy(String name, long val);

    // 减少计数
    public boolean decrBy(String name, long val);

    // 获取计数, 返回null表示数值不存在
    public Long getVal(String name);

    // 销毁接口
    public void destroy();
}
