package com.chihun.learn.example.javademo.concurrence;

import java.util.Properties;
import java.util.Set;

public class ThreadTask {
    private static boolean done = false;
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (!done) {
//                    Properties properties = System.getProperties();
//                    Set<String> names = properties.stringPropertyNames();
//                    if (null != names) {
//                        for (String name : names) {
//                            System.out.println(name + " : " + System.getProperty(name));
//                        }
//                    }
//                    System.out.println("===================================================");
                    i++;
                }
                System.out.println("Done!");
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        done = true;
        System.out.println("flag is set to done!");
    }
}
