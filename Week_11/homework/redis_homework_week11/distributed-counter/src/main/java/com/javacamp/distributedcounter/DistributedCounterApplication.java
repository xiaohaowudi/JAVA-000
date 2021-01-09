package com.javacamp.distributedcounter;

import com.javacamp.distributedcounter.counter.DistributedCounterTemplate;
import com.javacamp.distributedcounter.counter.SimpleRedisCounterTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.temporal.TemporalAccessor;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootApplication
public class DistributedCounterApplication {

    public static void main(String[] args) {
        // 10个增加计数器线程和10个减少计数器线程并发工作，验证最后计数值的数值是否正确
        SpringApplication.run(DistributedCounterApplication.class, args);

        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        List<Future<String>> futures = new LinkedList<>();

        // 先清理掉可能存在的Counter
        DistributedCounterTemplate temp= new SimpleRedisCounterTemplate("192.168.3.144", 6380);
        temp.deleteCounter("counter");

        for (int i = 0; i < 10; i++) {
            futures.add(threadPool.submit(
                () -> {
                    DistributedCounterTemplate template = new SimpleRedisCounterTemplate("192.168.3.144", 6380);
                    template.createCounter("counter", 100000);

                    for (int itr = 0; itr < 1000; itr++) {
                        if (!template.decrBy("counter", 10)) {
                            System.err.println("decr fail");
                        }
                    }

                    template.destroy();
                    System.out.println(Thread.currentThread().getName() + " over");
                    return "ok";
                }
            ));

            futures.add(threadPool.submit(
                () -> {
                    DistributedCounterTemplate template = new SimpleRedisCounterTemplate("192.168.3.144", 6380);
                    template.createCounter("counter", 100000);

                    for (int itr = 0; itr < 1000; itr++) {
                        if (!template.incrBy("counter", 10)) {
                            System.err.println("incr fail");
                        }
                    }

                    template.destroy();
                    System.out.println(Thread.currentThread().getName() + " over");
                    return "ok";
                }
            ));
        }


        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 检验最后答案
        System.out.println(temp.getVal("counter"));
        System.out.println(temp.getVal("counter") == 100000);
        System.out.println(temp.deleteCounter("counter"));
        temp.destroy();
        threadPool.shutdown();
    }

}
