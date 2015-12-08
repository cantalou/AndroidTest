package com.cantalou.test.skin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import com.cantalou.skin.SkinManager;
import com.cantalou.test.R;

public class SkinTwoActivity extends Activity
{

    private SkinManager skinManager = SkinManager.getInstance();

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        skinManager.onCreate(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.skin, menu);
        return true;
    }

}
