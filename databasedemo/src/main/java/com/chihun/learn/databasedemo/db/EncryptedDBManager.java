package com.chihun.learn.databasedemo.db;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class EncryptedDBManager implements IDatabaseManager {
    private static EncryptedDBManager manager;
    private SQLiteDatabase rddb;
    private SQLiteDatabase wtdb;
    private EncryptedDBHelper helper;
    private EncryptedDBManager(Context context){
        helper = new EncryptedDBHelper(context);
        rddb = helper.getReadableDatabase(EncryptedDBHelper.DB_PWD);
        wtdb = helper.getWritableDatabase(EncryptedDBHelper.DB_PWD);
    }

    public static EncryptedDBManager getManager(Context context) {
        if (null == manager) {
            manager = new EncryptedDBManager(context);
        }
        return manager;
    }

    public long insert(String content) {
        ContentValues cv = new ContentValues();
        cv.put("content", content);
        return wtdb.insert(EncryptedDBHelper.TABLE_NAME, null, cv);
    }

    public Cursor query() {
        return rddb.query(EncryptedDBHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    @Override
    public long update(String text) {
        return 0;
    }

    @Override
    public int delete() {
        return wtdb.delete(EncryptedDBHelper.TABLE_NAME, null, null);
    }

    public void closeDB() {
        wtdb.close();
        rddb.close();
    }

    class EncryptedDBHelper extends SQLiteOpenHelper {
        public static final String DB_NAME = "test.db";
        public static final int DB_VERSION = 1;
        public static final String DB_PWD="12345678";
        public static final String TABLE_NAME = "message";

        public EncryptedDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            SQLiteDatabase.loadLibs(context);
        }

        public EncryptedDBHelper(Context context) {
            this(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE message (content TEXT, "
                    + "sender TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }


    }
}
