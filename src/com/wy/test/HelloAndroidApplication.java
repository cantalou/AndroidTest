package com.wy.test;

import android.app.Application;

import com.wy.test.skin.SkinManager;

public class HelloAndroidApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        SkinManager.getInstance()
                   .init(this);
    }
}
