// IResultListener.aidl
package com.chihun.learn.servicedemo.service;
import com.chihun.learn.servicedemo.service.Result;
interface IResultListener {
    void onResult(out Result result);
}
