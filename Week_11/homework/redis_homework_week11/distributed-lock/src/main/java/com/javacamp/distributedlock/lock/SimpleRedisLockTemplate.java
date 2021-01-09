package com.javacamp.distributedlock.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


// 基于Redis的分布式锁简单实现
public class SimpleRedisLockTemplate implements DistributedLockTemplate {
    private String redisIp;     // redis服务器ip
    private int redisPort;      // redis服务器端口
    private Jedis jedis;

    public SimpleRedisLockTemplate(String redisIp, int redisPort) {
        this.redisIp = redisIp;
        this.redisPort = redisPort;
        this.jedis = new Jedis(redisIp, redisPort);
    }

    static String DEL_LUA =
                "if redis.call(\"get\", KEYS[1]) == ARGV[1] then\n"
            +   "    return redis.call(\"del\", KEYS[1])\n"
            +   "else\n"
            +   "    return 0\n"
            +   "end";

    @Override
    public String getLock(String lockName, Integer lockDurMilliSec, Integer waitMilliSec) {
        // 简单轮询获取锁, 每100ms轮询一次
        try {
            int iter_times = Math.max(1, waitMilliSec / 100);
            SetParams params = new SetParams();
            params.nx().px(lockDurMilliSec);
            String token = Long.toString(new Random().nextLong());

            for (int i = 0; i < iter_times; i++) {
                String ret = jedis.set("lock", token, params);
                if (ret != null && ret.equals("OK")) {
                    return token;
                }

                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @Override
    public boolean releaseLock(String lockName, String token) {
        if (token == null) {
            return false;
        }

        List<String> keys = new LinkedList<>();
        List<String> vals = new LinkedList<>();
        keys.add(lockName);
        vals.add(token);

        Long ret = (Long)(jedis.eval(DEL_LUA, keys, vals));
        return ret == 1;
    }

    @Override
    public void destroy() {
        this.jedis.close();
    }
}
