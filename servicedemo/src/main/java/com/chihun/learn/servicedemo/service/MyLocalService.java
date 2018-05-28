package com.chihun.learn.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyLocalService extends Service {
    public MyLocalService() {
    }

    public void sayHi() {
        Log.d("Service", "hello world!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public MyLocalService getService() {
            return MyLocalService.this;
        }
    }
}
