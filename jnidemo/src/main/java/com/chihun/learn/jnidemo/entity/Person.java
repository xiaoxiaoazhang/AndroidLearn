package com.chihun.learn.jnidemo.entity;

public class Person {
    public String name;
    public int age;
    public boolean male;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", male=" + male +
                '}';
    }
}
