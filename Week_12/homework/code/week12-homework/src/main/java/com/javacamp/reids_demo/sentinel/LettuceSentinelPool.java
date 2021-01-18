package com.javacamp.reids_demo.sentinel;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

// 基于Lettuce客户端的Sentinel连接池
public class LettuceSentinelPool {

    // Sentinal节点的ip和端口
    private String sentinelIp;
    private int sentinelPort;

    // sentinel id
    private String sentinelId;

    private GenericObjectPool<StatefulRedisConnection<String, String>> pool = null;

    private RedisClient client;

    public LettuceSentinelPool(String ip, int port, String id) {
        this.sentinelIp = ip;
        this.sentinelPort = port;
        this.sentinelId = id;

        RedisURI uri = RedisURI.builder()
                .withSentinel(sentinelIp, sentinelPort)
                .withSentinelMasterId(sentinelId)
                .build();

        this.client = RedisClient.create(uri);
        GenericObjectPoolConfig<StatefulRedisConnection<String, String>> config = new GenericObjectPoolConfig<StatefulRedisConnection<String, String>>();
        this.pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, config);
    }

    public StatefulRedisConnection<String, String> getConnection() throws Exception {
        return pool.borrowObject();
    }

    public void destory() {
        this.pool.close();
        client.shutdown();
    }
}
