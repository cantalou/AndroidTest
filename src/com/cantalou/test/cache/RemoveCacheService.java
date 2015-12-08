package com.cantalou.test.cache;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

public class RemoveCacheService extends Service
{

    private static final String TAG = "RemoveCacheService";

    private LruCache<Integer, String> cache;

    private Cache.Stub stub;

    @Override
    public void onCreate()
    {
        super.onCreate();

        cache = new LruCache<Integer, String>(10 * 1024 * 1024)
        {
            @Override
            protected int sizeOf(Integer key, String value)
            {
                // HashMapEntry(key,value,hashcode,next,8) = 24
                // key = Integer(int,8) = 16
                // value = String(offset,count,hashcode,char[],8) = 24
                // char[] = [C(length*2,8+4) = 12 + length*2
                // hashcode = 4
                // next = 4
                return 16 + 8 + 12 + value.length() * 2;
            }
        };

        stub = new Cache.Stub()
        {

            @Override
            public void put(int key, String value) throws RemoteException
            {
                if (TextUtils.isEmpty(value))
                {
                    return;
                }
                cache.put(key, value);
                List<byte[]> list = new ArrayList<byte[]>();
                Log.i(TAG, "start");
                while (true)
                {
                    list.add(new byte[1 * 1024 * 1024]);
                }
            }

            @Override
            public String get(int key) throws RemoteException
            {
                return cache.get(key);
            }
        };
        Log.i(TAG, "RemoveCacheService init ,pid:" + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return stub;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
