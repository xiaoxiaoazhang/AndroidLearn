package com.chihun.learn.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;

public class MyMessagerService extends Service {

    public static final int MSG_SAY_HELLO = 100;
    public static final int MSG_REPLY_HELLO = 101;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Log.d("MyMessagerService", "MSG_SAY_HELLO: " + msg.obj);
                    try {
                        Message message = Message.obtain();
                        message.what = MSG_REPLY_HELLO;
                        message.obj = "hi, world!";
                        msg.replyTo.send(message); //回复
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_REPLY_HELLO:
                    Log.d("MyMessagerService", "MSG_REPLY_HELLO: " + msg.obj);
                    break;
            }
        }
    };
    private Messenger messenger = new Messenger(handler);
    public MyMessagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
