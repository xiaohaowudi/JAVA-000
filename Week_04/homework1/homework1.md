## 作业1要求
思考有多少种方式，在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程？
写出你的方法，越多越好，提交到 Github

## 11种异步获取结果方法实现

### 方案1 Callable接口异步获取计算结果

```java

/*
方案1 线程池submit提交Callable接口实现类的实例，在实例中返回结果，主线程
用Future对象异步获取结果
*/

package Week3.homework;
import java.util.concurrent.*;

public class Solution1 {

    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        ExecutorService pool = Executors.newSingleThreadExecutor();

        Future<Integer> f = pool.submit(
                ()->{return sum();}
        );
        pool.shutdown();

        int result = 0; //这是得到的返回值
        try {
            result = f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```

### 方案2 FutureTask对象异步获取计算结果
```java

/*
方案2 线程池submit FutureTask对象，主线程用FutureTask的get方法获取结果
 */

package Week3.homework;

import java.util.concurrent.*;

public class Solution2 {

    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        FutureTask<Integer> ft = new FutureTask<>(()->{return sum();});

        pool.submit(ft);
        pool.shutdown();

        int result = 0; //这是得到的返回值
        try {
            result = ft.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}


```

### 方案3 Join方法进行线程同步
```java

/*
方案3 将结果对象传入匿名Runnable对象，在其run方法中对结果进行设置，主线程
等待计算线程结束，然后读取结果，利用堆中的公共对象传递结果
 */

package Week3.homework;


public class Solution3 {

    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        final int[] result = new int[1];

        Thread t = new Thread(() -> {result[0] = sum();});
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```

### 方案4 CompletableFuture异步获取计算结果
```java
/*
方案4 CompletableFuture异步获取结果
 */

package Week3.homework;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Solution4 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();

        int result = 0;
        try {
            result = CompletableFuture.supplyAsync(() -> { return sum();}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```


### 方案5 信号量控制线程同步
```java

/*
方案5 启动新线程计算结果，用Semaphone通知主线程计算结束
 */

package Week3.homework;

import java.util.concurrent.Semaphore;

public class Solution5 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        Semaphore s = new Semaphore(0);

        int[] result = new int[1];
        new Thread( () -> { result[0] = sum(); s.release();} ).start();
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```

### 方案6 LinkedBlockingQueue传递计算结果
```java


/*
方案6 启动新线程计算结果，用LinkedBlockingQueue传递计算结果，新线程计算
完成之前主线程阻塞在LinkedBlockingQueue获取数据的方法上
 */

package Week3.homework;

import java.util.concurrent.LinkedBlockingQueue;

public class Solution6 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        LinkedBlockingQueue<Integer> que = new LinkedBlockingQueue<Integer>();
        new Thread( () -> {
            try {
                que.put(sum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } ).start();


        int result = 0;
        try {
            result = que.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}


```

### 方案7 Condition条件变量控制线程同步

```java


/*
方案7 启动新线程计算结果，用Condition条件变量控制两个线程同步
 */

package Week3.homework;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Solution7 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        Lock lock = new ReentrantLock();
        Condition calcFinish = lock.newCondition();
        int[] result = new int[1];

        new Thread( () -> { lock.lock(); result[0] = sum(); calcFinish.signal(); lock.unlock(); } ).start();
        try {
            lock.lock();
            calcFinish.await();
            lock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}


```


### 方案8 CyclicBarrier控制线程同步

```java


/*
方案8 启动新线程计算结果，用CyclicBarrier控制两个线程同步
 */

package Week3.homework;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Solution8 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(2);
        int[] result = new int[1];

        new Thread( () -> { result[0] = sum();
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        } ).start();

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```

### 方案9 CountdownLatch控制线程同步

```java

package Week3.homework;

/*
方案9 启动新线程计算结果，用CountdownLatch控制两个线程同步
 */


import java.util.concurrent.CountDownLatch;

public class Solution9 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);
        int[] result = new int[1];

        new Thread( () -> { result[0] = sum(); latch.countDown(); } ).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}


```

### 方案10 Exchanger交换计算结果
```java

package Week3.homework;

/*
方案10 启动新线程计算结果，用Exchanger交换主线程和计算线程的数据
 */

import java.util.concurrent.Exchanger;

public class Solution10 {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();

        Exchanger<Integer> exchanger = new Exchanger<>();
        new Thread( () -> {
            try {
                exchanger.exchange(sum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } ).start();

        int result = 0;
        try {
            result = exchanger.exchange(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```

### 方案11 利用Object类的wait notify机制进行线程同步

```java

package Week3.homework;

/*
方案11 启动新线程计算结果，用Object对象的wait notify进行同步
 */

public class Solution11 {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        int[] result = new int[1];
        new Thread( () -> { synchronized (Solution11.class) { result[0] = sum(); Solution11.class.notify(); } } ).start();

        try {
            synchronized (Solution11.class) {
                Solution11.class.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+ result[0]);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

```








