package com.javacamp.reids_demo;

import com.javacamp.reids_demo.cluster.ClusterJedis;
import com.javacamp.reids_demo.cluster.ClusterLettucePool;
import com.javacamp.reids_demo.cluster.RedissonCluster;
import com.javacamp.reids_demo.redisson.RedissonSentinel;
import com.javacamp.reids_demo.sentinel.LettuceSentinelPool;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.junit.jupiter.api.Test;


import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class Week12HomeWorkApplicationTests {

    @Test
    void testLettuceSentinel() {
        LettuceSentinelPool pool = new LettuceSentinelPool("192.168.3.144", 26379, "mymaster");
        try (StatefulRedisConnection<String, String> conn = pool.getConnection()) {
            RedisCommands<String, String> commands = conn.sync();
            String ret = commands.set("hello", "world123");
            System.out.println(ret);
            assertEquals("world123", commands.get("hello"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        pool.destory();
    }

    @Test
    void testRedissonSentinel() {
        List<String> sentinelNodes = new LinkedList<>();
        sentinelNodes.add("192.168.3.144:26379");
        sentinelNodes.add("192.168.3.144:26380");
        sentinelNodes.add("192.168.3.144:26381");

        RedissonSentinel redissonSentinel = new RedissonSentinel(sentinelNodes, "mymaster");
        try {
            RedissonClient client = redissonSentinel.getClient();
            RBucket<String> bucket = client.getBucket("hello");

            bucket.set("world222");
            assertEquals("world222", bucket.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testClusterJedis() {
        List<HostAndPort> hostAndPortList = new LinkedList<>();
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7000));
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7001));
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7002));
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7003));
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7004));
        hostAndPortList.add(new HostAndPort("192.168.3.144", 7005));

        JedisCluster cluster = ClusterJedis.getJedisCluster(hostAndPortList);
		for (int i = 0; i < 100; i++) {
			cluster.set("cluster:" + i, "data:" + i);
		}

		for (int i = 0; i < 100; i++) {
		    assertEquals(cluster.get("cluster:"+i), "data:"+i);
        }

		cluster.close();
    }

    @Test
    void testClusterLettuce() {
        ClusterLettucePool clusterLettucePool = null;
        try {
            clusterLettucePool = new ClusterLettucePool("192.168.3.144", 7000);

            try (StatefulRedisClusterConnection<String, String> conn = clusterLettucePool.getConnection()) {
                RedisAdvancedClusterCommands<String, String> commands = conn.sync();
                for (int i = 0; i < 100; i++) {
                    commands.set("cluster:" + i, "data_lettuce:" + i);
                }

                for (int i = 0; i < 100; i++) {
                    assertEquals(commands.get("cluster:" + i), "data_lettuce:" + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clusterLettucePool != null) {
                clusterLettucePool.destroy();
            }
        }
    }

    @Test
    void testClusterRedisson() {
        List<String> clusterNodes = new LinkedList<>();
        clusterNodes.add("192.168.3.144:7000");
        clusterNodes.add("192.168.3.144:7001");
        clusterNodes.add("192.168.3.144:7002");
        clusterNodes.add("192.168.3.144:7003");
        clusterNodes.add("192.168.3.144:7004");
        clusterNodes.add("192.168.3.144:7005");

        RedissonCluster redissonCluster = new RedissonCluster(clusterNodes);
        RedissonClient client = null;
        try {
            client = redissonCluster.getClient();

            for (int i = 0; i < 100; i++) {
                RBucket<String> bucket = client.getBucket("cluster:" + i);
                bucket.set("data_cluster:" + i);
                assertEquals("data_cluster:" + i, bucket.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }
}
