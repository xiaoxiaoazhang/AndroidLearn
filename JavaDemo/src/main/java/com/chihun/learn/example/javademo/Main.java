package com.chihun.learn.example.javademo;

public class Main {

    private void bitOperation() {
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

    public static void main(String[] args) {
        String byteStr = "[12,34,34,87]";
        System.out.println("byteArray: " + Arrays.stringToByteArray(byteStr));
        System.out.println("byteStr: " + Arrays.byteArrayToString(new byte[]{12,34,34,87}));
    }


}
