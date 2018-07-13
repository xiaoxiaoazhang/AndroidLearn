package com.chihun.learn.databasedemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.tencent.wcdb.DatabaseUtils;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;
import com.tencent.wcdb.database.SQLiteStatement;

import java.io.File;


public class PlainTextDBManager implements IDatabaseManager {

    private static PlainTextDBManager manager;
    private SQLiteDatabase rddb;
    private SQLiteDatabase wtdb;
    private PlainTextDBHelper helper;
    private PlainTextDBManager(Context context){
        helper = new PlainTextDBHelper(context);
        rddb = helper.getReadableDatabase();
        wtdb = helper.getWritableDatabase();
    }

    public static PlainTextDBManager getManager(Context context) {
        if (null == manager) {
            manager = new PlainTextDBManager(context);
        }
        return manager;
    }

    public long insert(String content) {
        ContentValues cv = new ContentValues();
        cv.put("content", content);
        return wtdb.insert(WCEncryptedDBManager.WCEncryptedDBHelper.TABLE_NAME, null, cv);
    }

    public Cursor query() {
        return rddb.query(WCEncryptedDBManager.WCEncryptedDBHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    @Override
    public long update(String text) {
        return 0;
    }

    @Override
    public int delete() {
        return wtdb.delete(EncryptedDBManager.EncryptedDBHelper.TABLE_NAME, null, null);
    }

    public void closeDB() {
        wtdb.close();
        rddb.close();
    }

    static class PlainTextDBHelper extends SQLiteOpenHelper {
        private static final String TAG = PlainTextDBHelper.class.getSimpleName();
        private static final String DATABASE_NAME = "plain-text.db";
        private static final int DATABASE_VERSION = 1;
        private Context mContext;

        public PlainTextDBHelper(Context context) {
            // Call "plain-text" version of the superclass constructor.
            super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            File oldDbFile = mContext.getDatabasePath(DATABASE_NAME);
            if (oldDbFile.exists()) {
                Log.d("PlainTextDBHelper", "old database exist: " + oldDbFile.getAbsolutePath());

            }
            db.execSQL("CREATE TABLE message (content TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Do nothing.
        }

        /**
         * 方法1：检查某表列是否存在
         * @param db
         * @param tableName 表名
         * @param columnName 列名
         * @return
         */
        private boolean checkColumnExist1(SQLiteDatabase db, String tableName
                , String columnName) {
            boolean result = false;
            Cursor cursor = null;
            try {
                //查询一行
                cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                        , null);
                result = cursor != null && cursor.getColumnIndex(columnName) != -1;
            } catch (Exception e) {
                Log.e(TAG, "checkColumnExists1..." + e.getMessage());
            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            return result;
        }


        /**
         * 方法2：通过查询sqlite的系统表 sqlite_master 来查找相应表里是否存在该字段，稍微换下语句也可以查找表是否存在
         * @param db
         * @param tableName 表名
         * @param columnName 列名
         * @return
         */
        private boolean checkColumnExists2(SQLiteDatabase db, String tableName
                , String columnName) {
            boolean result = false ;
            Cursor cursor = null ;
            try{
                cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                        , new String[]{tableName , "%" + columnName + "%"} );
                result = null != cursor && cursor.moveToFirst() ;
            }catch (Exception e){
                Log.e(TAG,"checkColumnExists2..." + e.getMessage()) ;
            }finally{
                if(null != cursor && !cursor.isClosed()){
                    cursor.close() ;
                }
            }
            return result ;
        }

        /**
         * 获取表结构
         * @param db
         * @param tableName
         */
        private void tableInfo(SQLiteDatabase db, String tableName) {
            String sql = "PRAGMA table_info(?)";
            String tableInfo = DatabaseUtils.stringForQuery(db, sql, new String[]{tableName});
            Log.d(TAG, "tableInfo: " + tableInfo);
        }

    }
}
