package com.cantalou.test.performance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cantalou.test.HelloAndroidActivity;

class DBHelper extends SQLiteOpenHelper
{

    private static final String TAG = "DBHelper";

    private SQLiteDatabase wdb;

    private SQLiteStatement insert;

    private SQLiteStatement query;

    public DBHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        wdb = getWritableDatabase();
        insert = wdb.compileStatement("insert into test(id,value) values(?,?)");
        query = wdb.compileStatement("select value from test where id = ?");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table test(id integer , value text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void put(Integer key, String value)
    {
        insert.clearBindings();
        insert.bindLong(1, key);
        insert.bindString(2, value);
        insert.executeInsert();
        //		insert.close();
    }

    public String get(Integer key)
    {
        query.clearBindings();
        query.bindLong(1, key);
        String result = query.simpleQueryForString();
        //		query.close();
        return result;
    }

}

class FileHelper
{

    private static final String TAG = "FileHelper";

    public static void put(Context cxt, Integer key, String value)
    {
        BufferedOutputStream bos = null;
        try
        {
            bos = new BufferedOutputStream(cxt.openFileOutput(Integer.toString(key), Context.MODE_PRIVATE));
            bos.write(value.getBytes("GBK"));
            bos.flush();
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }
        finally
        {
            try
            {
                bos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String get(Context cxt, Integer key)
    {
        BufferedInputStream bis = null;
        try
        {
            bis = new BufferedInputStream(cxt.openFileInput(Integer.toString(key)));
            int len = bis.available();
            byte[] content = new byte[len];
            bis.read(content);
            return new String(content, "GBK");
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }
        finally
        {
            try
            {
                bis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }
}

class SharePreferenceCommitHelper
{

    private static final String TAG = "SharePreferenceCommitHelper";

    public static void put(Context cxt, Integer key, String value)
    {
        SharedPreferences sp = cxt.getSharedPreferences("Commit", Context.MODE_PRIVATE);
        sp.edit()
          .putString(key.toString(), value)
          .commit();
    }

    public static String get(Context cxt, Integer key)
    {
        SharedPreferences sp = cxt.getSharedPreferences("Commit", Context.MODE_PRIVATE);
        return sp.getString(key.toString(), "");
    }
}

class SharePreferenceApplyHelper
{

    private static final String TAG = "SharePreferenceApplyHelper";

    public static void put(Context cxt, Integer key, String value)
    {
        SharedPreferences sp = cxt.getSharedPreferences("Apply", Context.MODE_PRIVATE);
        sp.edit()
          .putString(key.toString(), value)
          .apply();
    }

    public static String get(Context cxt, Integer key)
    {
        SharedPreferences sp = cxt.getSharedPreferences("Apply", Context.MODE_PRIVATE);
        return sp.getString(key.toString(), "");
    }
}

public class SharedPreferenceDbFileTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity>
{

    private static final String TAG = "SharedPreferenceDbFileTest";

    private Context cxt;

    private DBHelper helper;

    public SharedPreferenceDbFileTest()
    {
        super(HelloAndroidActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        cxt = getActivity();
        helper = new DBHelper(cxt, "test.db", null, 1);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (helper != null)
        {
            helper.close();
        }
    }

    final int count = 1000;

    final Integer key = 123;

    final String value = "123";

    public void testSharedPreferenceDbFile()
    {
        testCommitSharedPreference();
        testApplySharedPreference();
        testDb();
        testFile();
    }

    public void testCommitSharedPreference()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            SharePreferenceCommitHelper.put(cxt, key, value);
        }
        Log.i(TAG, "CommitSharedPreference.put :" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            SharePreferenceCommitHelper.get(cxt, key);
        }
        Log.i(TAG, "CommitSharedPreference.get :" + (System.currentTimeMillis() - start));
    }

    public void testApplySharedPreference()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            SharePreferenceApplyHelper.put(cxt, key, value);
        }
        Log.i(TAG, "ApplySharedPreference.put :" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            SharePreferenceApplyHelper.get(cxt, key);
        }
        Log.i(TAG, "ApplySharedPreference.get :" + (System.currentTimeMillis() - start));
    }

    public void testDb()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            helper.put(key, value);
        }
        Log.i(TAG, "db.put :" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            helper.get(key);
        }
        Log.i(TAG, "db.get :" + (System.currentTimeMillis() - start));
        helper.close();
    }

    public void testFile()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            FileHelper.put(cxt, key, value);
        }
        Log.i(TAG, "file.put :" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            FileHelper.get(cxt, key);
        }
        Log.i(TAG, "file.get :" + (System.currentTimeMillis() - start));
    }

}
