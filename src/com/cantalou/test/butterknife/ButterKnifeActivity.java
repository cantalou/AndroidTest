package com.cantalou.test.butterknife;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;

public class ButterKnifeActivity extends Activity
{

    public static class ButtonCust extends Button
    {
        public ButtonCust(Context context)
        {
            super(context);
        }
    }

    @InjectView(id.btn_test)
    protected ButtonCust test;

    @InjectView(id.btn_test1)
    public Button test1;

    @InjectViews({id.btn_test, id.btn_test1})
    public Button[] buttons;

    @OnClick(id.btn_test)
    public void test1(View v)
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.butter_knife);
        ButterKnife.inject(this);
        Log.i("", test.toString());
    }

}
