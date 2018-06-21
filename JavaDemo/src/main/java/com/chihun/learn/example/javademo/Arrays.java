package com.chihun.learn.example.javademo;

public class Arrays {

    public static String byteArrayToString(byte[] a) {
        if (a == null)
            return "";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(",");
        }
    }

    public static byte[] stringToByteArray(String byteStr) {
        if (byteStr == null || byteStr.equals("")) {
            return null;
        }
        if ("[]".equals(byteStr)) {
            return new byte[0];
        }
        byte[] byteArray = null;
        if (byteStr.startsWith("[") && byteStr.endsWith("]")) {
            String[] byteArrayStr = byteStr.substring(1, byteStr.length() - 1).split(",");
            byteArray = new byte[byteArrayStr.length];
            for (int i = 0; i < byteArrayStr.length; i++) {
                byteArray[i] = Byte.parseByte(byteArrayStr[i]);
            }
        }
        return byteArray;
    }
}
