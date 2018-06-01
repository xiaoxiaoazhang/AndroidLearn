package com.chihun.learn.providerdemo;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.chihun.learn.providerdemo.db.DBHelper;
import com.chihun.learn.providerdemo.provider.MyContentProvider;

public class MainActivity extends AppCompatActivity {

    private DBHelper mDBHelper;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    queryPerson();
                    break;
                case 2:
                    queryAddress();
                    break;

            }
        }
    };
    /**
     * 定义一个内容观察者
     */
    private ContentObserver mContentObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            Log.e("MainActivity", "onChange: =======+>>>>>>>>>>>>selfChange is " + selfChange);
            mHandler.sendEmptyMessage(1);
        }
    };

    private ContentObserver mContentObserver2 = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            Log.e("MainActivity2", "onChange: =======+>>>>>>>>>>>>selfChange is " + selfChange);
            mHandler.sendEmptyMessage(2);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = DBHelper.getInstance(this);

        //注册内容观察者,它监听的是MyContentProvider.CONTENT_URI，对应的字符串是＂content://com.chihun.provider＂
        //根据Uri的格式如下
        //scheme://host:port/path
        //该Uri对应的host为com.chihun.provider,也就是我们在AndroidManifest.xml自定义MyContentProvider的authorities
        //所以该CONTENT_URI对应的就是MyContentProvider提供的资源
        getContentResolver().registerContentObserver(MyContentProvider.PERSON_URI,true,mContentObserver);
        getContentResolver().registerContentObserver(MyContentProvider.ADDRESS_URI,true,mContentObserver2);
        /**
         * 顺循环想数据库中插入数据
         * getContentResolver().insert第一个参数为MyContentProvider.CONTENT_URI,
         * 因为MyContentProvider.CONTENT_URI对应的是MyContentProvider的资源，所以
         * 该insert的方法会调用到MyContentProvider的insert
         * 在insert方法里面再进行数据库操作．
         * 插入成功的话，再调用notifyChange方法通知观察者
         */
        new Thread() {
            @Override
            public void run() {
                int i = 5;
                mDBHelper.deleteAll(DBHelper.Column.TABLE_NAME);
                mDBHelper.deleteAll(DBHelper.Column.TABLE_NAME2);
                while (i > 0)
                {
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.Column.COLUMN_ID, i);
                    values.put(DBHelper.Column.COLUMN_NAME, i + "");
                    getContentResolver().insert(MyContentProvider.PERSON_URI, values);

                    ContentValues address = new ContentValues();
                    address.put(DBHelper.Column.COLUMN_ID, i);
                    address.put(DBHelper.Column.COLUMN_ADDRESS, i + "_");
                    getContentResolver().insert(MyContentProvider.ADDRESS_URI, address);
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
            }
        }.start();
    }

    private void queryPerson() {
        Cursor cursor = getContentResolver().query(MyContentProvider.PERSON_URI, null, null, null, null);
        if (cursor != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = cursor.getColumnCount();
            Log.d("Count" , "count: " + cursor.getCount() + " columnCount: " + count);
            String[] columnNames = cursor.getColumnNames();
            if (null != columnNames && columnNames.length > 0) {
                stringBuilder.delete(0, stringBuilder.length());
                for (String name : columnNames) {
                    stringBuilder.append(name).append(" | ");
                }
                Log.d("Colume" , stringBuilder.toString());
            } else {
                Log.d("Colume" , "null");
            }
            while (cursor.moveToNext()) {
                if (count > 0) {
                    stringBuilder.delete(0, stringBuilder.length());
                    for (int index = 0; index < count; index++) {
                        stringBuilder.append(cursor.getString(index)).append(" | ");
                    }
                    Log.d("Value" , stringBuilder.toString());
                } else {
                    Log.d("Value" , "null");
                }
            }
        }
        cursor.close();
    }

    private void queryAddress() {
        Cursor cursor = getContentResolver().query(MyContentProvider.ADDRESS_URI, null, null, null, null);
        if (cursor != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = cursor.getColumnCount();
            Log.d("AddressCount" , "count: " + cursor.getCount() + " columnCount: " + count);
            String[] columnNames = cursor.getColumnNames();
            if (null != columnNames && columnNames.length > 0) {
                stringBuilder.delete(0, stringBuilder.length());
                for (String name : columnNames) {
                    stringBuilder.append(name).append(" | ");
                }
                Log.d("Address Colume" , stringBuilder.toString());
            } else {
                Log.d("Address Colume" , "null");
            }
            while (cursor.moveToNext()) {
                if (count > 0) {
                    stringBuilder.delete(0, stringBuilder.length());
                    for (int index = 0; index < count; index++) {
                        stringBuilder.append(cursor.getString(index)).append(" | ");
                    }
                    Log.d("Address Value" , stringBuilder.toString());
                } else {
                    Log.d("Address Value" , "null");
                }
            }
        }
        cursor.close();
    }


    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(mContentObserver);
        getContentResolver().unregisterContentObserver(mContentObserver2);
        super.onDestroy();
    }
}
