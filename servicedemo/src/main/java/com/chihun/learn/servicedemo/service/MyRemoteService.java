package com.chihun.learn.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Map;

public class MyRemoteService extends Service {

    private static final String TAG = MyRemoteService.class.getSimpleName();

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
        public void onStart(Map param, IResultListener listener, IBinder binder) throws RemoteException {

            //监听客户端死亡信息,当客户端异常断开连接后会有回调，服务端可以释放资源
            RecognitionClient client = new RecognitionClient((String) param.get("pkgName"), binder);
            client.linkToDeath();

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

        @Override
        public void onStart2(String param, IResultListener listener) throws RemoteException {

        }

        @Override
        public void onStart3(byte[] param, IResultListener listener) throws RemoteException {

        }

        @Override
        public void onStart4(Bundle param, IResultListener listener) throws RemoteException {

        }
    }

    private class RecognitionClient implements IBinder.DeathRecipient{

        private IBinder mToken;
        private String mPackageName;

        public RecognitionClient(String packageName, IBinder token){
            this.mToken = token;
            this.mPackageName = packageName;
        }

        public void linkToDeath() throws RemoteException{
            if(null == mToken){
                return;
            }
            mToken.linkToDeath(this, 0);
        }

        public void unlinkToDeath(){
            if(null == mToken){
                return;
            }
            mToken.unlinkToDeath(this, 0);
        }

        @Override
        public void binderDied() {
            //取消监听
            unlinkToDeath();
            //TODO 进行对应请求的退出动作
            Log.d(TAG, "client is died! package: " + mPackageName);
        }
    }
}
