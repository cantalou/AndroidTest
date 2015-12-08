package com.cantalou.test.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends IntentService
{

    private Thread runningThread;

    private volatile boolean running = true;

    public interface Callback
    {
        public void callback(Object o);
    }

    private Callback callback;

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public BackgroundService(String name)
    {
        super(name);
    }

    public BackgroundService()
    {
        super("BackgroundService");
    }

    public class CallbackBinder extends Binder
    {
        BackgroundService getSrevice()
        {
            return BackgroundService.this;
        }
    }

    public CallbackBinder callbackBinder = new CallbackBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i("", "onBind");
        return callbackBinder;
    }

    @Override
    public void onCreate()
    {
        Log.i("", "onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Log.i("", "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.i("", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
        Log.i("", "onDestroy");
        running = false;
        if (null != runningThread && runningThread.getState()
                                                  .equals(Thread.State.TIMED_WAITING))
        {
            runningThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        runningThread = Thread.currentThread();
        int i = 0;
        while (running)
        {
            try
            {
                Thread.sleep(2000);
                Log.i("", "running");
            }
            catch (InterruptedException e)
            {
                Log.i("", "线程被打断");
            }
            callback.callback(i++);
        }

    }

}
