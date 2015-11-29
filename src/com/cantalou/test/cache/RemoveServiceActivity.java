package com.cantalou.test.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.cantalou.test.R;
import com.cantalou.test.R.id;

public class RemoveServiceActivity extends Activity
{

    private static final String TAG = "RemoveServiceActivity";

    private Cache cache = null;

    @InjectView(id.key)
    public EditText key;

    @InjectView(id.value)
    public EditText value;

    LinkedHashMap<Integer, String> localCache = new LinkedHashMap<Integer, String>();

    private ServiceConnection conn = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            cache = Cache.Stub.asInterface(service);
        }
    };

    final int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_service);

        ButterKnife.inject(this);

        Intent i = new Intent(this, RemoveCacheService.class);
        startService(i);
        bindService(i, conn, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "RemoveServiceActivity init ,pid:" + Process.myPid());
    }

    @OnClick(id.put)
    public void put()
    {

        try
        {
            int keyInt = Integer.parseInt(key.getText()
                                             .toString());
            String valueStr = value.getText()
                                   .toString();
            long start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                cache.put(keyInt, valueStr);
            }
            long end = System.currentTimeMillis();
            Log.i(TAG, "remote time : " + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                localCache.put(keyInt, valueStr);
            }
            end = System.currentTimeMillis();
            Log.i(TAG, "local time : " + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                put(keyInt, valueStr);
            }
            end = System.currentTimeMillis();
            Log.i(TAG, "file time : " + (end - start));

            key.setText("");
            value.setText("");
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @OnClick(id.get)
    public void get()
    {
        String valueStr = null;
        try
        {
            int keyInt = Integer.parseInt(key.getText()
                                             .toString());

            long start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                valueStr = cache.get(keyInt);
            }
            long end = System.currentTimeMillis();
            Log.i(TAG, "remote time : " + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                valueStr = localCache.get(keyInt);
            }
            end = System.currentTimeMillis();
            Log.i(TAG, "local time : " + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++)
            {
                valueStr = get(keyInt);
            }
            end = System.currentTimeMillis();
            Log.i(TAG, "file time : " + (end - start));

            value.setText("rv:" + valueStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Intent i = new Intent(this, RemoveCacheService.class);
        stopService(i);
        unbindService(conn);
    }

    public void put(Integer key1, String value1)
    {
        BufferedOutputStream bos = null;
        try
        {
            bos = new BufferedOutputStream(this.openFileOutput(Integer.toString(key1), Context.MODE_PRIVATE));
            bos.write(value1.getBytes("GBK"));
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

    public String get(Integer key1)
    {
        BufferedInputStream bis = null;
        try
        {
            bis = new BufferedInputStream(this.openFileInput(Integer.toString(key1)));
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
