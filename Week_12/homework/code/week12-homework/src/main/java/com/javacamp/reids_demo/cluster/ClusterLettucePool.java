package com.javacamp.reids_demo.cluster;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ClusterLettucePool {

    private String clusterNodeIp;
    private int clusterNodePort;
    private RedisClusterClient client;
    private GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool;

    public ClusterLettucePool(String ip, int port) throws Exception {
        this.clusterNodeIp = ip;
        this.clusterNodePort = port;

        RedisURI uri = RedisURI.builder().withHost(this.clusterNodeIp).withPort(this.clusterNodePort).build();

        this.client = RedisClusterClient.create(uri);
        GenericObjectPoolConfig<StatefulRedisClusterConnection<String, String>> config = new GenericObjectPoolConfig<>();
        this.pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, config);
    }

    public StatefulRedisClusterConnection<String, String> getConnection() throws Exception {
        return this.pool.borrowObject();
    }

    public void destroy() {
        this.pool.close();
        this.client.shutdown();
    }
}
