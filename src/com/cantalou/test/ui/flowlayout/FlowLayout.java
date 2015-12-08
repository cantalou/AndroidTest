package com.cantalou.test.ui.flowlayout;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.*;

public final class FlowLayout extends ViewGroup
{

    public FlowLayout(Context context)
    {
        super(context);
        initView();
    }

    public FlowLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView()
    {

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private ArrayList<ArrayList<View>> arrangedView;

    private ArrayList<Integer> arrangedViewLineHeight;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int childCount = getChildCount();
        if (childCount == 0)
        {
            return;
        }
        else if (arrangedView == null)
        {
            arrangedView = new ArrayList<ArrayList<View>>();
            arrangedViewLineHeight = new ArrayList<Integer>();
        }
        else
        {
            arrangedView.clear();
            arrangedViewLineHeight.clear();
        }
        arrangedView.add(new ArrayList<View>());
        arrangedViewLineHeight.add(0);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int resultWidth = 0;
        int resultHeight = 0;

        int currentLineWidth = 0;

        for (int i = 0, len = childCount; i < len; i++)
        {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
            {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (child.getLayoutParams() instanceof MarginLayoutParams)
            {
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                childWidth += mlp.leftMargin + mlp.rightMargin;
                childHeight += mlp.topMargin + mlp.bottomMargin;
            }

            if ((currentLineWidth += childWidth) <= widthSize)
            {
                int index = arrangedView.size() - 1;
                arrangedView.get(index)
                            .add(child);
                if (arrangedViewLineHeight.get(index) < childHeight)
                {
                    arrangedViewLineHeight.set(index, childHeight);
                }
            }
            else
            {
                if ((currentLineWidth -= childWidth) > resultWidth)
                {
                    resultWidth = currentLineWidth;
                }
                resultHeight += arrangedViewLineHeight.get(arrangedViewLineHeight.size() - 1);

                currentLineWidth = childWidth;
                arrangedViewLineHeight.add(childHeight);
                ArrayList<View> newLine = new ArrayList<View>(2);
                newLine.add(child);
                arrangedView.add(newLine);
            }

            if (resultWidth == 0 || resultWidth < currentLineWidth)
            {
                resultWidth = currentLineWidth;
                resultHeight = arrangedViewLineHeight.get(arrangedViewLineHeight.size() - 1);
            }
        }

        if (EXACTLY == widthMode)
        {
            resultWidth = widthSize;
        }
        if (EXACTLY == heightMode)
        {
            resultHeight = heightSize;
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {

        if (arrangedView == null || arrangedView.size() == 0)
        {
            return;
        }

        for (int i = 0, len = arrangedView.size(); i < len; i++)
        {
            ArrayList<View> lineViews = arrangedView.get(i);
            int lineHeight = arrangedViewLineHeight.get(i);
            int left = l;
            for (View child : lineViews)
            {
                if (child.getLayoutParams() instanceof MarginLayoutParams)
                {
                    MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                    left = left + mlp.leftMargin;
                    int top = t + mlp.topMargin;
                    child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                    left = left + child.getMeasuredWidth() + mlp.rightMargin;
                }
                else
                {
                    child.layout(l, t, l + child.getMeasuredWidth(), t + child.getMeasuredHeight());
                    left = left + child.getMeasuredWidth();
                }
            }
            t += lineHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (int i = 0, len = getChildCount(); i < len; i++)
        {
            getChildAt(i).draw(canvas);
        }
    }
}