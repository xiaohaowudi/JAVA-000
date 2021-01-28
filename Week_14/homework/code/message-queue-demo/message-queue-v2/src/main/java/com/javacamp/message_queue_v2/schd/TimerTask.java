package com.javacamp.message_queue_v2.schd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@EnableScheduling
@Slf4j
public class TimerTask {

    private final Set<TimerPollable> pollableObjs = new ConcurrentSkipListSet<>();

    public void registerPollable(TimerPollable pollable) {
        pollableObjs.add(pollable);
    }

    @Scheduled(fixedRate = 100)
    public void timerPoll100ms() {
        pollableObjs.forEach((pollable) -> {
            pollable.pollWithTimer(100L);
        });
    }

}
