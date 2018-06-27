// IServiceControl.aidl
package com.chihun.learn.servicedemo.service;

import java.lang.String;
import java.util.Map;
import com.chihun.learn.servicedemo.service.IResultListener;
// 不支持同名函数
interface IServiceControl {
    void onStart(in Map param, IResultListener listener, IBinder binder);
    void onStart2(in String param, IResultListener listener);
    void onStart3(in byte[] param, IResultListener listener);
    void onStart4(in Bundle param, IResultListener listener);
}
