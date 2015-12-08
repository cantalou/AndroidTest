package com.cantalou.test.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cantalou.test.HelloAndroidActivity;

public class SqliteTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity>
{

    private static final String TAG = "SqliteTest";

    private DBHelper dbHelper;

    public SqliteTest()
    {
        super(HelloAndroidActivity.class);
    }

    public SqliteTest(Class<HelloAndroidActivity> activityClass)
    {
        super(HelloAndroidActivity.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        this.getActivity()
            .deleteDatabase("test.db");
        dbHelper = DBHelper.getInsrance(getActivity());
    }

    public void tearDown() throws Exception
    {
        dbHelper.close();
    }

    private int[] sizes = new int[]{1, 5, 10, 50, 100, 500, 700, 1000, 2500, 5000, 7500, 10000, 20000};

    public void testReadAndReadWrire()
    {
        for (int size : sizes)
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            insertData(db, size);
            db.close();

            db = dbHelper.getReadableDatabase();
            doQuery(db, "getReadableDatabase", size, 1000);

            dbHelper.getWritableDatabase();
            doQuery(db, "getWritableDatabase", size, 1000);
        }

    }


    public void testIndexPerformace()
    {
        doTestTask(dbHelper.getWritableDatabase(), " ", 50000, 1000);
        testNoIndexPerformace();
        testUniqueIndexPerformace();
        testNornalIndexPerformace();
    }

    public void testNoIndexPerformace()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int size : sizes)
        {
            doTestTask(db, "没有索引", size, 1000);
        }
    }

    public void testUniqueIndexPerformace()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("create unique index i_users_id on users(_id)");
        for (int size : sizes)
        {
            doTestTask(db, "唯一索引", size, 1000);
        }
    }

    public void testNornalIndexPerformace()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("drop index i_users_id");
        db.execSQL("create index i_users_id on users(_id)");
        for (int size : sizes)
        {
            doTestTask(db, "普通索引", size, 1000);
        }
    }

    private void insertData(SQLiteDatabase db, int size)
    {
        db.beginTransaction();
        try
        {
            db.execSQL("delete from users");
            for (int i = 0; i < size; i++)
            {
                db.execSQL("insert into users(name,gender,age,phoneNumber,address) values( 'name', 1, 1, 'tel', 'address')");
            }
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    private void doTestTask(SQLiteDatabase db, String tag, int size, int times)
    {
        insertData(db, size);
        doQuery(db, tag, size, times);
    }

    private void doQuery(SQLiteDatabase db, String tag, int size, int times)
    {
        String sql1 = "select * from users where _id = " + (1 + size >> 1);
        String sql2 = "select count(_id) from users where _id > " + (size - 1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
        {
            Cursor c = db.rawQuery(sql1, null);
            c.moveToNext();
            c.getInt(0);
            c.close();

            c = db.rawQuery(sql2, null);
            c.moveToNext();
            c.getInt(0);
            c.close();
        }
        Log.i(TAG, tag + " size：" + size + " , time:" + (System.currentTimeMillis() - start));
    }
}
