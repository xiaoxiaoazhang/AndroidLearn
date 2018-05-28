// IServiceControl.aidl
package com.chihun.learn.servicedemo.service;

import java.lang.String;
import java.util.Map;
import com.chihun.learn.servicedemo.service.IResultListener;

interface IServiceControl {
    void onStart(in Map param, IResultListener listener);
}
