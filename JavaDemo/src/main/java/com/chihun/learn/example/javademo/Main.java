package com.chihun.learn.example.javademo;

public class Main {

    public static void main(String[] args) {
        int flag = 0;

        flag |= 0x10;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag += 0x11;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag &= 0;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag &= 0x11;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag += 0x11;

        System.out.println("flag=" + Integer.toHexString(flag));
    }
}
