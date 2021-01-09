package com.javacamp.distributedlock;

import com.javacamp.distributedlock.lock.DistributedLockTemplate;
import com.javacamp.distributedlock.lock.SimpleRedisLockTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootApplication
public class DistributedLockApplication {


    static Long cnt = 0L;

    public static void main(String[] args) {
        SpringApplication.run(DistributedLockApplication.class);

        // 10个线程模拟10个独立进程对数据进行累加，验证分布式锁功能
        final Long cnt = 0L;

        ExecutorService threadPoll = Executors.newFixedThreadPool(10);
        List<Future<String>> futures = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(threadPoll.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    DistributedLockTemplate lock = new SimpleRedisLockTemplate("192.168.3.144", 6380);

                    for (long i = 0; i < 1000; i++) {
                        String token = lock.getLock("lock", 10000, 10000);
                        if (token == null) {
                            System.err.println("get lock fail");
                            continue;
                        }

                        DistributedLockApplication.cnt += 1;
                        lock.releaseLock("lock", token);
                    }

                    lock.destroy();

                    System.out.println(Thread.currentThread() + " over");
                    return "ok";
                }
            }));
        }

        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadPoll.shutdown();

        // 验证最终答案是否正确
        System.out.println("DistributedLockApplication.cnt = " + DistributedLockApplication.cnt);
        System.out.println(DistributedLockApplication.cnt == 10 * 1000);

    }

}
