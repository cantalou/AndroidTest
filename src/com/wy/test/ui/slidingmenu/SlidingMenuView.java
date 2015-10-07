package com.wy.test.ui.slidingmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenuView extends HorizontalScrollView
{

    private ViewGroup container;

    private int padding = 50;

    private int screenWidth;

    private int leftRightWidth;

    public SlidingMenuView(Context context)
    {
        this(context, null);
        init();
    }

    public SlidingMenuView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SlidingMenuView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressLint("InflateParams")
    private void init()
    {
        container = (ViewGroup) LayoutInflater.from(getContext())
                                              .inflate(com.wy.test.R.layout.sliding_menu_view, null);
        addView(container);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay()
          .getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        leftRightWidth = screenWidth - padding;

        container.getChildAt(0)
                 .getLayoutParams().width = leftRightWidth;
        container.getChildAt(1)
                 .getLayoutParams().width = screenWidth;
        container.getChildAt(2)
                 .getLayoutParams().width = leftRightWidth;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        this.scrollTo(leftRightWidth, 0);
    }

    private long startTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                startTime = ev.getEventTime();
                break;
            case MotionEvent.ACTION_UP:
                long endTime = ev.getEventTime();
                int scrollX = getScrollX();
                float distance = 0;
                if (scrollX > screenWidth / 2 && scrollX < screenWidth * 1.4)
                {
                    distance = Math.abs(leftRightWidth - scrollX);
                    Log.i("", "1.scrollX : " + scrollX + ", leftRightWidth " + leftRightWidth + ",distance : " + distance);
                    ObjectAnimator oa = ObjectAnimator.ofInt(this, "scrollX", scrollX, leftRightWidth);
                    oa.setDuration((long) (300 * distance / leftRightWidth));
                    oa.start();
                }
                else if (scrollX > screenWidth * 1.4)
                {
                    distance = Math.abs(leftRightWidth * 2 - scrollX);
                    Log.i("", "2.scrollX : " + scrollX + ", leftRightWidth " + leftRightWidth + ",distance : " + distance);
                    ObjectAnimator oa = ObjectAnimator.ofInt(this, "scrollX", scrollX, leftRightWidth * 2);
                    oa.setDuration((long) (300 * distance / leftRightWidth));
                    oa.start();
                }
                else
                {
                    distance = scrollX;
                    Log.i("", "3.scrollX : " + scrollX + ", leftRightWidth " + leftRightWidth + ",distance : " + distance);
                    ObjectAnimator oa = ObjectAnimator.ofInt(this, "scrollX", scrollX, 0);
                    oa.setDuration((long) (300 * scrollX / leftRightWidth));
                    oa.start();
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        ViewHelper.setTranslationX(container.getChildAt(0), l);
    }

    public void openLeft()
    {

    }

    public void openRight()
    {

    }
}
