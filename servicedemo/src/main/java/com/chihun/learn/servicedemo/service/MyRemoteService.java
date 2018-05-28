package com.chihun.learn.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Map;

public class MyRemoteService extends Service {
    public MyRemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends IServiceControl.Stub {

        @Override
        public void onStart(Map param, IResultListener listener) throws RemoteException {
            String action = (String) param.get("action");
            if ("success".equals(action)) {
                Result result = new Result();
                result.setCode(200);
                result.setMessage("success");
                listener.onResult(result);
            } else if ("fail".equals(action)) {
                Result result = new Result();
                result.setCode(404);
                result.setMessage("fail");
                listener.onResult(result);
            } else {
                Result result = new Result();
                result.setCode(500);
                result.setMessage("unknow fail");
                listener.onResult(result);
            }
        }
    }
}
