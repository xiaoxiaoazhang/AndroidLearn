package com.chihun.learn.example.javademo;

public class Main {

    public static void main(String[] args) {
        int flag = 0;

        flag |= 0x10;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag |= 0x11;
        flag &= 0x0F;
        System.out.println("flag=" + Integer.toHexString(flag));

        flag |= 0x20;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag |= 0x22;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag |= 0x11;

        System.out.println("flag=" + Integer.toHexString(flag));

        flag &= 0xF0;
        System.out.println("flag=" + Integer.toHexString(flag));
    }
}
