package com.javacamp.byte_buddy_aop;

public class Func {

    // 用于被AOP增强的函数
    public Integer func() {
        int sum = 0;
        for (int i = 1; i <= 1000; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sum += i;
        }
        return sum;
    }
}
