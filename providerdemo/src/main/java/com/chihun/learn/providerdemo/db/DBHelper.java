package com.chihun.learn.providerdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.chihun.learn.providerdemo.db.DBHelper.Column.COLUMN_ADDRESS;
import static com.chihun.learn.providerdemo.db.DBHelper.Column.COLUMN_AGE;
import static com.chihun.learn.providerdemo.db.DBHelper.Column.COLUMN_ID;
import static com.chihun.learn.providerdemo.db.DBHelper.Column.COLUMN_NAME;
import static com.chihun.learn.providerdemo.db.DBHelper.Column.TABLE_NAME;
import static com.chihun.learn.providerdemo.db.DBHelper.Column.TABLE_NAME2;

public class DBHelper {
    private static DBHelper mDBHelper;

    private SQLiteDatabase mWriteDb;
    private SQLiteDatabase mReadDb;

    private DBHelper(Context context) {
        MyDatabaseHelper myHelper = new MyDatabaseHelper(context);
        mWriteDb = myHelper.getWritableDatabase();
        mReadDb = myHelper.getReadableDatabase();
    }

    public static DBHelper getInstance(final Context context) {
        if (mDBHelper == null) {
            synchronized (DBHelper.class) {
                if (mDBHelper == null) {
                    mDBHelper = new DBHelper(context);
                }
            }
        }

        return mDBHelper;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return mWriteDb;
    }

    public SQLiteDatabase getReadSQLiteDatabase() {
        return mReadDb;
    }

    /**
     * 插入一个数据库项
     * @param values
     * @return
     */
    public long insert(ContentValues values, String tableName) {
        return mWriteDb.insert(tableName,null,values);
    }

    public void deleteAll(String tableName) {
        mWriteDb.delete(tableName,null,null);
    }

    public void delete(String tableName, int id) {
        mWriteDb.delete(tableName,Column.COLUMN_ID + "=?",new String[]{String.valueOf(id)});
    }

    private class MyDatabaseHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION = 2;
        private static final String DB_NAME = "mydb";

        private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT ," +
                COLUMN_AGE + " INTEGER ) ";

        private static final String CREATE_TABLE_SQL2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ADDRESS + " TEXT ) ";

        private MyDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_SQL);
            db.execSQL(CREATE_TABLE_SQL2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
            onCreate(db);
        }
    }

    public static class Column {
        public static final String TABLE_NAME = "person";
        public static final String TABLE_NAME2 = "address";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "_name";
        public static final String COLUMN_AGE = "_age";
        public static final String COLUMN_ADDRESS = "_address";
    }
}
