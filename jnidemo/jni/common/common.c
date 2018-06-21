//
// Created by zhenduowang on 2018/6/21.
//
#include "common.h"

int add(int a, int b) {
    return a+b;
}

int sum(int a[]) {
    if(a == NULL) {
        return 0;
    }
    int size = sizeof(a);
    int sum = 0;
    for (int i = 0; i < size; ++i) {
        sum += a[i];
    }
    return sum;
}
