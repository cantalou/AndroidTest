package com.wy.test.memory;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.util.Log;

public class MaxMemoryActivity extends Activity
{

    ActivityManager am;

    List<byte[]> ref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable ex)
            {
                try
                {
                    android.os.Debug.dumpHprofData("/data/data/com.wy.test/log/dump.hprof");
                }
                catch (IOException e)
                {
                    Log.w("MaxMemoryActivity", "getMemoryClass : " + am.getMemoryClass());
                }
            }
        });

        ref = new ArrayList<byte[]>();
        while (true)
        {
            ref.add(new byte[1024 * 1024]);
        }

    }

}
