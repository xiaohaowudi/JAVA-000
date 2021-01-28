package com.javacamp.message_queue_v2.schd;

public interface TimerPollable extends Comparable<Object> {
    void pollWithTimer(Long periodMilliSec);

    @Override
    default int compareTo(Object o) {
        return this.hashCode() - o.hashCode();
    }
}
