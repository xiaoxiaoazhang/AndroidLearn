package com.chihun.learn.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chihun.learn.servicedemo.service.IResultListener;
import com.chihun.learn.servicedemo.service.IServiceControl;
import com.chihun.learn.servicedemo.service.MyLocalService;
import com.chihun.learn.servicedemo.service.MyMessagerService;
import com.chihun.learn.servicedemo.service.MyRemoteService;
import com.chihun.learn.servicedemo.service.Result;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RemoteServiceConnection remoteServiceConnection;
    private MessagerServiceConnection messagerServiceConnection;
    private LocalServiceConnection localServiceConnection;
    private Messenger messenngerService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (remoteServiceConnection == null) {
                    remoteServiceConnection = new RemoteServiceConnection();
                }
                Intent intent = new Intent(this, MyRemoteService.class);
                bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.button2:
                unbindService(remoteServiceConnection);
                break;
            case R.id.button3:
                if (localServiceConnection == null) {
                    localServiceConnection = new LocalServiceConnection();
                }
                Intent intent2 = new Intent(this, MyLocalService.class);
                bindService(intent2, localServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.button4:
                unbindService(localServiceConnection);
                break;
            case R.id.button5:
                if (null == messagerServiceConnection) {
                    messagerServiceConnection = new MessagerServiceConnection();
                }
                Intent intent3 = new Intent(this, MyMessagerService.class);
                bindService(intent3, messagerServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.button6:
                unbindService(messagerServiceConnection);
                break;
        }
    }

    //调用此方法时会发送信息给服务端
    public void sayHello() {
        //发送一条信息给服务端
        Message msg = Message.obtain(handler, MyMessagerService.MSG_SAY_HELLO, 0, 0, "hi, bruce");
        try {
            msg.replyTo = messenger;
            messenngerService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private IResultListener resultListener = new IResultListener.Stub() {
        @Override
        public void onResult(Result result) throws RemoteException {
            Log.d("MainActivity", "result: " + result.getCode() + " " + result.getMessage());
        }
    };
    private IServiceControl serviceControl;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d("MainActivity", "binderDied is called! service is died!");
            if (serviceControl == null) {
                return;
            }
            serviceControl.asBinder().unlinkToDeath(mDeathRecipient, 0);
            serviceControl = null;
            // TODO:重新绑定远程服务
            bindService(new Intent(MainActivity.this, MyRemoteService.class), remoteServiceConnection, BIND_AUTO_CREATE);
        }
    };


   // 服务端异常退出会有 pingBinder——> binderDied——>onServiceDisconnected——>RemoteException的顺序执行，可以通过监听上述的方法监听服务端是否停止服务
    private class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IServiceControl serviceControl = IServiceControl.Stub.asInterface(service);
            HashMap<String, String> param = new HashMap<>();
            param.put("pkgName", getPackageName());
            param.put("action", "success");
            try {
                if (service.pingBinder()) {
                    Log.d("MainActivity", "服务端连接成功");
                } else {
                    Log.d("MainActivity", "服务端连接失败");
                }
                // 监听服务端是否异常断开，
                service.linkToDeath(mDeathRecipient, 0);

                serviceControl.onStart(param, resultListener, new Binder());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 当service端异常时会调用，正常结束不会调用
            // TODO:重新绑定远程服务
//            bindService(new Intent(MainActivity.this, MyRemoteService.class), remoteServiceConnection, BIND_AUTO_CREATE);
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("MainActivity", "message: " + msg.what+ " " + msg.obj);
        }
    };
    private Messenger messenger = new Messenger(handler);

    private class MessagerServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //接收onBind()传回来的IBinder，并用它构造Messenger
            messenngerService = new Messenger(service);
            sayHello();
        }

        public void onServiceDisconnected(ComponentName className) {
            messenngerService = null;
        }
    }

    private MyLocalService myLocalService;

    private class LocalServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //接收onBind()传回来的IBinder，并用它构造Messenger
            MyLocalService.MyBinder myBinder = (MyLocalService.MyBinder)service;
            myLocalService = myBinder.getService();
            myLocalService.sayHi();
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    }
}
