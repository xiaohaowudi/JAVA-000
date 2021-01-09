package com.javacamp.distributedcounter.counter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import sun.reflect.annotation.ExceptionProxy;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

public class SimpleRedisCounterTemplate implements DistributedCounterTemplate {
    private String redisIp;
    private int redisPort;
    private Jedis jedis;

    public SimpleRedisCounterTemplate(String ip, int port) {
        this.redisIp = ip;
        this.redisPort = port;
        this.jedis = new Jedis(ip, port);
    }

    static String INCR_LUA =
            "if redis.call(\"exists\", KEYS[1]) == 1 then\n" +
            "    return redis.call(\"incrby\", KEYS[1], ARGV[1])\n" +
            "else\n" +
            "    return -1\n" +
            "end";

    static String DECR_LUA =
            "if redis.call(\"exists\", KEYS[1]) == 1 then\n" +
            "    if tonumber(redis.call(\"get\", KEYS[1])) < tonumber(ARGV[1]) then\n" +
            "        return -1\n" +
            "    else\n" +
            "        return redis.call(\"decrby\", KEYS[1], ARGV[1])\n" +
            "    end\n" +
            "else\n" +
            "    return -1\n" +
            "end";

    @Override
    public boolean createCounter(String name, long initVal) {
        SetParams params = new SetParams();
        String ret = jedis.set(name, String.valueOf(initVal), params.nx());
        return ret != null && ret.equals("OK");
    }

    @Override
    public boolean deleteCounter(String name) {
        return jedis.del(name) == 1;
    }

    private boolean execOneParaLua(String lua, String name, long val) {
        try {
            List<String> keys = new LinkedList<>();
            List<String> vals = new LinkedList<>();

            keys.add(name);
            vals.add(Long.toString(val));
            Long ret = (Long)jedis.eval(lua, keys, vals);
            if (ret == -1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    @Override
    public boolean incrBy(String name, long val) {
        if (val < 0) {
            return false;
        }

        return execOneParaLua(INCR_LUA, name, val);
    }

    @Override
    public boolean decrBy(String name, long val) {
        if (val < 0) {
            return false;
        }

        return execOneParaLua(DECR_LUA, name, val);
    }

    @Override
    public Long getVal(String name) {

        Long ret = null;
        try {
            ret = Long.valueOf(jedis.get(name));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    @Override
    public void destroy() {
        this.jedis.close();
    }

    public static void main(String[] args) {
        DistributedCounterTemplate template = new SimpleRedisCounterTemplate("192.168.3.144", 6380);
        System.out.println(template.createCounter("counter", 100L));
        System.out.println(template.incrBy("counter", 100L));
        System.out.println(template.decrBy("counter", 50));
        System.out.println(template.getVal("counter"));
        System.out.println(template.deleteCounter("counter"));

        template.destroy();
    }
}
