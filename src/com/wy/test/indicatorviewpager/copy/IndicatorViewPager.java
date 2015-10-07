package com.wy.test.indicatorviewpager.copy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.wy.test.R.id;
import com.wy.test.R.layout;

public class IndicatorViewPager extends FragmentActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.indicator_fragment_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(id.content, new NestFragment());
        ft.commit();
    }

}
