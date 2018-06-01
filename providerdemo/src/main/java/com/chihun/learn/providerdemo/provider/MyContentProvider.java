package com.chihun.learn.providerdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.chihun.learn.providerdemo.db.DBHelper;

public class MyContentProvider extends ContentProvider {

    private static final int TABLE_PERSON_DIR = 1;
    private static final int TABLE_PERSON_ITEM = 2;
    private static final int TABLE_ADDRESS_DIR = 3;
    private static final int TABLE_ADDRESS_ITEM = 4;

    private static final String AUTHORITIES = "com.chihun.provider";
    private static final String PATH_PERSON = "person";
    private static final String PATH_ADDRESS = "address";
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // 查询指定ID的一条数据
    public static final Uri PERSON_URI = Uri.parse("content://" + AUTHORITIES + "/" + PATH_PERSON + "/" + TABLE_PERSON_DIR);
    // 查询指定表数据
    public static final Uri ADDRESS_URI = Uri.parse("content://" + AUTHORITIES + "/" + PATH_ADDRESS);

    static {
        mUriMatcher.addURI(AUTHORITIES, PATH_PERSON, TABLE_PERSON_DIR);
        mUriMatcher.addURI(AUTHORITIES, PATH_PERSON + "/#", TABLE_PERSON_ITEM);
        mUriMatcher.addURI(AUTHORITIES, PATH_ADDRESS, TABLE_ADDRESS_DIR);
        mUriMatcher.addURI(AUTHORITIES, PATH_ADDRESS + "/#", TABLE_ADDRESS_ITEM);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        DBHelper mDBHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = mDBHelper.getReadSQLiteDatabase();
        Cursor cursor = null;

        switch (mUriMatcher.match(uri)){
            case TABLE_PERSON_DIR:
                cursor = db.query(DBHelper.Column.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TABLE_PERSON_ITEM:
                String userId = uri.getPathSegments().get(1);
                cursor = db.query(DBHelper.Column.TABLE_NAME, projection, "_id = ?", new String[] { userId }, null, null, sortOrder);
                break;

            case TABLE_ADDRESS_DIR:
                cursor = db.query(DBHelper.Column.TABLE_NAME2, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TABLE_ADDRESS_ITEM:
                String faceId = uri.getPathSegments().get(1);
                cursor = db.query(DBHelper.Column.TABLE_NAME2, projection, "_id = ?", new String[] { faceId }, null, null, sortOrder);
                break;

            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)){
            case TABLE_PERSON_DIR:
                return "vnd.android.cursor.dir/vnd." + AUTHORITIES + "." + PATH_PERSON;

            case TABLE_PERSON_ITEM:
                return "vnd.android.cursor.item/vnd." + AUTHORITIES + "." + PATH_PERSON;

            case TABLE_ADDRESS_DIR:
                return "vnd.android.cursor.dir/vnd." + AUTHORITIES + "." + PATH_ADDRESS;

            case TABLE_ADDRESS_ITEM:
                return "vnd.android.cursor.item/vnd." + AUTHORITIES + "." + PATH_ADDRESS;

            default:
                break;
        }
        return null;
    }

    /**
     * 当通过getContentResolver().insert(MyContentProvider.CONTENT_URI,values)的方式插入数据库
     * 会调用到此处，此时我们将其插入数据库并且
     * 通知观察者数据　发生变化
     * @param uri 要操作的资源uri
     * @param values 要插入的内容
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        DBHelper mDBHelper = DBHelper.getInstance(getContext());
        Uri result = null;

        switch (mUriMatcher.match(uri)){
            case TABLE_PERSON_DIR:
            case TABLE_PERSON_ITEM:
                long row = mDBHelper.insert(values, DBHelper.Column.TABLE_NAME);
                if(row != -1) {
                    result = Uri.parse("content://" + AUTHORITIES + "/" + DBHelper.Column.TABLE_NAME + "/" + row);
                    getContext().getContentResolver().notifyChange(result,null);
                }
                break;

            case TABLE_ADDRESS_DIR:
            case TABLE_ADDRESS_ITEM:
                long row2 = mDBHelper.insert(values, DBHelper.Column.TABLE_NAME2);
                if(row2 != -1) {
                    result = Uri.parse("content://" + AUTHORITIES + "/" + DBHelper.Column.TABLE_NAME2 + "/" + row2);
                    getContext().getContentResolver().notifyChange(result,null);
                }
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        DBHelper mDBHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = mDBHelper.getSQLiteDatabase();
        int deletedRows = 0;

        switch (mUriMatcher.match(uri)) {
            case TABLE_PERSON_DIR:
                deletedRows = db.delete(DBHelper.Column.TABLE_NAME, selection, selectionArgs);
                break;

            case TABLE_PERSON_ITEM:
                String id = uri.getPathSegments().get(1);
                deletedRows = db.delete(DBHelper.Column.TABLE_NAME, "_id = ?", new String[]{id});
                break;

            case TABLE_ADDRESS_DIR:
                deletedRows = db.delete(DBHelper.Column.TABLE_NAME2, selection, selectionArgs);
                break;

            case TABLE_ADDRESS_ITEM:
                String id2 = uri.getPathSegments().get(1);
                deletedRows = db.delete(DBHelper.Column.TABLE_NAME2, "_id = ?", new String[]{id2});
                break;

            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        DBHelper mDBHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = mDBHelper.getSQLiteDatabase();
        int updatedRows = 0;

        switch (mUriMatcher.match(uri)){
            case TABLE_PERSON_DIR:
                updatedRows = db.update(DBHelper.Column.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TABLE_PERSON_ITEM:
                String personID = uri.getPathSegments().get(1);
                updatedRows = db.update(DBHelper.Column.TABLE_NAME, values, "_id = ?", new String[] { personID });
                break;

            case TABLE_ADDRESS_DIR:
                updatedRows = db.update(DBHelper.Column.TABLE_NAME2, values, selection, selectionArgs);
                break;

            case TABLE_ADDRESS_ITEM:
                String addressID = uri.getPathSegments().get(1);
                updatedRows = db.update(DBHelper.Column.TABLE_NAME2, values, "_id = ?", new String[] { addressID });
                break;

            default:
                break;
        }

        return updatedRows;
    }
}
