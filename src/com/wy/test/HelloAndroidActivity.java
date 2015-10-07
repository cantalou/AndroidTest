package com.wy.test;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wy.test.R.id;
import com.wy.test.animation.ArcAnimationActivity;
import com.wy.test.animation.RotateImageActivity;
import com.wy.test.animation.RotateImageActivity2;
import com.wy.test.butterknife.ButterKnifeActivity;
import com.wy.test.file.FileActivity;
import com.wy.test.image.ImageActivity;
import com.wy.test.service.HandleWithBackgroundServiceActivity;
import com.wy.test.sqlite.SqliteActvity;
import com.wy.test.ui.DrawRingActivity;
import com.wy.test.ui.DrawRingArcActivity;
import com.wy.test.ui.ToastInNotMainThradActivity;
import com.wy.test.ui.flowlayout.FlowLayoutActivity;
import com.wy.test.ui.listview.CustCommonAdapterActivity;
import com.wy.test.ui.slidingmenu.SlidingMenuActivity;

public class HelloAndroidActivity extends Activity
{

    LinearLayout container;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i("HelloAndroidActivity", "onCreate , pid:" + Process.myPid());

        setContentView(R.layout.activity_main);
        container = (LinearLayout) findViewById(id.container);

        try
        {
            PackageInfo info = this.getPackageManager()
                                   .getPackageInfo(this.getPackageName(), 1);
            for (ActivityInfo ai : info.activities)
            {

                final Button b = new Button(this);
                final String name = ai.name;
                b.setText(name.substring(name.lastIndexOf(".") + 1));
                container.addView(b, 0);

                b.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            final Class<?> klass = Class.forName(name);
                            Intent i = new Intent(getApplicationContext(), klass);
                            HelloAndroidActivity.this.startActivity(i);
                        }
                        catch (ClassNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                    }
                });

            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        Log.i("HelloAndroidActivity", "onResume");
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        Log.i("HelloAndroidActivity", "onStart");
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        Log.i("HelloAndroidActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Log.i("HelloAndroidActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        Log.i("HelloAndroidActivity", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("HelloAndroidActivity", "onDestroy");
        super.onDestroy();
    }
}
