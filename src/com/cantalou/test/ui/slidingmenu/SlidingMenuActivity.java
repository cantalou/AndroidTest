package com.cantalou.test.ui.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;

public class SlidingMenuActivity extends FragmentActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.sliding_menu_activity);

        setLeft(new LeftFragment());
        setMain(new MainFragment());
        setRight(new LeftFragment());
    }

    public void setLeft(Fragment f)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(id.left, f);
        ft.commit();
    }

    public void setRight(Fragment f)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(id.right, f);
        ft.commit();
    }

    public void setSpace(int space)
    {

    }

    public void setMain(Fragment f)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(id.main, f);
        ft.commit();
    }

}
