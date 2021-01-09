package com.javacamp.distributedlock.lock;


// 锁接口
public interface DistributedLockTemplate {
    // 获取锁接口，获取名称为lockName, 生命周期为lockDurMilliSec的锁, 成功返回token, 失败返回null, 同步接口，至多等待waitMilliSec毫秒
    public String getLock(String lockName, Integer lockDurMilliSec, Integer waitMilliSec);

    // 释放锁接口, 释放名称为lockName的锁
    public boolean releaseLock(String lockName, String token);

    // 销毁锁
    public void destroy();
}
