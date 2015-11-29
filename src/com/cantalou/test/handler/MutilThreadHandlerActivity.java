package com.cantalou.test.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class MutilThreadHandlerActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Handler h = new Handler();
        h.sendMessage(null);

    }

}
