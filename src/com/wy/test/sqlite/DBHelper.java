package com.wy.test.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{

    private static Context cxt;

    private static class InstanceHolder
    {
        public static DBHelper instance = new DBHelper(cxt);
    }

    public static DBHelper getInsrance(Context context)
    {
        cxt = context;
        return InstanceHolder.instance;
    }

    private DBHelper(Context context)
    {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists users(" + "_id INTEGER PRIMARY KEY," + "name TEXT," + "gender INTEGER," + "age INTEGER," + "phoneNumber TEXT," + "address TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }


}