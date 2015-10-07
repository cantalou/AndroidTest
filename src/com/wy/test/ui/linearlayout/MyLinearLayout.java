package com.wy.test.ui.linearlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout
{

    public MyLinearLayout(Context context)
    {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
