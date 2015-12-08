package com.cantalou.test.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class OpenAppFromBrowserActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent i = new Intent("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://172.31.255.14");
        i.setData(content_url);
        startActivity(i);
    }

    @Override
    protected void onResume()
    {
        Log.i("OpenAppFromBrowserActivity", "onResume");
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        Log.i("OpenAppFromBrowserActivity", "onStart");
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        Log.i("OpenAppFromBrowserActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Log.i("OpenAppFromBrowserActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("OpenAppFromBrowserActivity", "onDestroy");
        super.onDestroy();
    }
}
