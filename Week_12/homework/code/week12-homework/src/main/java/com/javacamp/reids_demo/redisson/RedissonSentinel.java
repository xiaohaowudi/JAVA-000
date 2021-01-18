package com.javacamp.reids_demo.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;

import java.util.List;

public class RedissonSentinel {

    final private List<String> hostPortList;
    final private String sentinelId;

    public RedissonSentinel(List<String> hostPortOfSentinelNodes, String id) {
        this.hostPortList = hostPortOfSentinelNodes;
        this.sentinelId = id;
    }

    public RedissonClient getClient() throws Exception {
        Config config = new Config();
        SentinelServersConfig sentinelConfig = config.useSentinelServers().setMasterName(this.sentinelId);
        for (String hostPort : this.hostPortList) {
            sentinelConfig.addSentinelAddress("redis://" + hostPort);
        }
        return Redisson.create(config);
    }
}
