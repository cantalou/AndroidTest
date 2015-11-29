package com.cantalou.test;

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

import com.cantalou.test.R;
import com.cantalou.test.R.id;
import com.cantalou.test.animation.ArcAnimationActivity;
import com.cantalou.test.animation.RotateImageActivity;
import com.cantalou.test.animation.RotateImageActivity2;
import com.cantalou.test.butterknife.ButterKnifeActivity;
import com.cantalou.test.file.FileActivity;
import com.cantalou.test.image.ImageActivity;
import com.cantalou.test.service.HandleWithBackgroundServiceActivity;
import com.cantalou.test.sqlite.SqliteActvity;
import com.cantalou.test.ui.DrawRingActivity;
import com.cantalou.test.ui.DrawRingArcActivity;
import com.cantalou.test.ui.ToastInNotMainThradActivity;
import com.cantalou.test.ui.flowlayout.FlowLayoutActivity;
import com.cantalou.test.ui.listview.CustCommonAdapterActivity;
import com.cantalou.test.ui.slidingmenu.SlidingMenuActivity;

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
