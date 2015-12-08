package com.cantalou.test.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

public class ToastInNotMainThradActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable()
        {
            public void run()
            {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "adf", Toast.LENGTH_SHORT)
                     .show();
                Looper.loop();
            }
        }).start();
    }

}
