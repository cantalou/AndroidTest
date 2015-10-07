package com.wy.test.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.wy.test.R.id;
import com.wy.test.R.layout;

public class HandleWithBackgroundServiceActivity extends Activity
{

    private static TextView count;

    static Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.hwbsa);
        count = (TextView) findViewById(id.count);
    }

    private ServiceConnection sc = new ServiceConnection()
    {
        public void onServiceDisconnected(ComponentName name)
        {

        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            ((BackgroundService.CallbackBinder) service).getSrevice()
                                                        .setCallback(new BackgroundService.Callback()
                                                        {
                                                            public void callback(final Object o)
                                                            {
                                                                h.post(new Runnable()
                                                                {
                                                                    public void run()
                                                                    {
                                                                        count.setText(o.toString());
                                                                    }
                                                                });
                                                            }
                                                        });
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent i = new Intent(this, BackgroundService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.bindService(i, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause()
    {
        Log.i("", "onPause");
        super.onPause();
        this.unbindService(sc);
    }

    @Override
    protected void onStop()
    {
        Log.i("", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("", "activity onDestroy");
        super.onDestroy();
    }

}
