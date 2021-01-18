package com.javacamp.reids_demo.cluster;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;

import java.util.List;

public class RedissonCluster {
    final private List<String> hostPortList;

    public RedissonCluster(List<String> hostPortOfSentinelNodes) {
        this.hostPortList = hostPortOfSentinelNodes;
    }

    public RedissonClient getClient() throws Exception {
        Config config = new Config();
        ClusterServersConfig sentinelConfig = config.useClusterServers();
        for (String hostPort : this.hostPortList) {
            sentinelConfig.addNodeAddress("redis://" + hostPort);
        }
        return Redisson.create(config);
    }
}
