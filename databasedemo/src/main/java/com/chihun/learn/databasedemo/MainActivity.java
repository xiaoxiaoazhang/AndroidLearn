package com.chihun.learn.databasedemo;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chihun.learn.databasedemo.db.IDatabaseManager;
import com.chihun.learn.databasedemo.db.WCEncryptedDBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    IDatabaseManager manager;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = WCEncryptedDBManager.getManager(this);
//        manager = EncryptedDBManager.getManager(this);
//        manager = PlainTextDBManager.getManager(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.closeDB();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_insert:
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        int i = 10000;
                        long start = System.currentTimeMillis();
                        while (i-- > 0) {
                            Log.i("MainActivity", "insert text_:" + i + (0 < manager.insert("text_" + i) ? "success":"fail"));
                        }
                        Log.i("MainActivity", "spend time: " + (System.currentTimeMillis() - start));
                    }
                });
                break;
            case R.id.btn_query:
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        Cursor cursor = manager.query();
                        if (null != cursor) {
                            while (cursor.moveToNext()) {
                                Log.i("MainActivity", "query context: " + cursor.getString(cursor.getColumnIndex("content")));
                            }
                            cursor.close();
                        }
                        Log.i("MainActivity", "spend time: " + (System.currentTimeMillis() - start));
                    }
                });
                break;
            case R.id.btn_clear:
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        manager.delete();
                        Log.i("MainActivity", "spend time: " + (System.currentTimeMillis() - start));
                    }
                });
                break;
        }
    }
}
