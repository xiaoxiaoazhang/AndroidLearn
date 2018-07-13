package com.chihun.learn.example.javademo.collection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class BlockingQueueTest {

    private BlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1024);// 上限固定
    private BlockingQueue delayQueue = new DelayQueue();
    private BlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
    private BlockingQueue queue = new PriorityBlockingQueue();
    private BlockingQueue syncQueue = new SynchronousQueue();

    public void test() {

    }

    // 生产者
    public class Producer implements Runnable{

        protected BlockingQueue queue = null;

        public Producer(BlockingQueue queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                queue.put("1");
                Thread.sleep(1000);
                queue.put("2");
                Thread.sleep(1000);
                queue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 消费者
    public class Consumer implements Runnable{

        protected BlockingQueue queue = null;

        public Consumer(BlockingQueue queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                System.out.println(queue.take());
                System.out.println(queue.take());
                System.out.println(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
