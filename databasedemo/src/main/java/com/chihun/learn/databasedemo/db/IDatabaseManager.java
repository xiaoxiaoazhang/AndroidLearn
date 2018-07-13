package com.chihun.learn.databasedemo.db;

import android.database.Cursor;

public interface IDatabaseManager {

    long insert(String text);

    Cursor query();

    long update(String text);

    int delete();

    void closeDB();
}
