package com.javacamp.message_queue_v2.broker;

import com.javacamp.message_queue_core_api.broker.RandomAccessBroker;
import com.javacamp.message_queue_v2.schd.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class RandomAccessBrokerFactory {

    @Autowired
    private TimerTask timerTask;

    final ConcurrentHashMap<String, RandomAccessBrokerImpl> id2broker = new ConcurrentHashMap<>();
    final ReentrantLock deleteLock = new ReentrantLock();

    public RandomAccessBroker createBroker(String brokerId) {

        synchronized (id2broker) {
            try {
                id2broker.putIfAbsent(brokerId, new RandomAccessBrokerImpl());
                timerTask.registerPollable(id2broker.get(brokerId));
                return id2broker.get(brokerId);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    public boolean deleteBroker(String brokerId) {
        deleteLock.lock();
        try {
            synchronized (id2broker) {
                if (!id2broker.containsKey(brokerId)) {
                    return false;
                }

                id2broker.remove(brokerId);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            deleteLock.unlock();
        }

        return false;
    }

    // 暂停所有Broker 删除行为，直到解锁为止
    public void pauseFactoryDeleteOper() {
        deleteLock.lock();
    }

    // 恢复所有Broker 删除行为
    public void resumeFactoryDeleteOper() {
        deleteLock.unlock();
    }

}
