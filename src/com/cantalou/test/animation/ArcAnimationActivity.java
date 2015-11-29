package com.cantalou.test.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.cantalou.test.R.layout;

public class ArcAnimationActivity extends Activity
{

    static class ShapeHolder
    {

        public View mTarget;

        public ShapeHolder(View mTarget)
        {
            super();
            this.mTarget = mTarget;
        }

        public void setWidth(int width)
        {
            mTarget.getLayoutParams().width = width;
        }

        public void setHeight(int height)
        {
            mTarget.getLayoutParams().height = height;
        }

        public void getX()
        {
            ViewHelper.getX(mTarget);
        }

        public void setX(int f)
        {
            ViewHelper.setX(mTarget, f);
        }

        public void getY()
        {
            ViewHelper.getY(mTarget);
        }

        public void setY(int f)
        {
            ViewHelper.setY(mTarget, f);
        }
    }

    private ShapeHolder[] views = new ShapeHolder[4];

    AnimatorSet as = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.arc_button);

        int widthBound = 150;
        long duration = 300;

        for (int i = 1; i < 5; i++)
        {
            final ImageView iv = (ImageView) findViewById(getResources().getIdentifier("iv_" + i, "id", this.getPackageName()));
            views[i - 1] = new ShapeHolder(iv);

            ObjectAnimator oaWidth = ObjectAnimator.ofInt(views[i - 1], "width", 1, 50);
            oaWidth.setDuration(duration);
            oaWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    iv.requestLayout();
                }
            });

            ObjectAnimator oaHeight = ObjectAnimator.ofInt(views[i - 1], "height", 1, 50);
            oaHeight.setDuration(duration);

            double delta = Math.PI / 2 / (views.length - 1) * (i - 1);
            int x = (int) (widthBound * Math.cos(delta));
            ObjectAnimator oaX = ObjectAnimator.ofInt(views[i - 1], "x", 1, x);
            oaX.setDuration(duration);

            int y = (int) (widthBound * Math.sin(delta));
            ObjectAnimator oaY = ObjectAnimator.ofInt(views[i - 1], "y", 1, y);
            oaY.setDuration(duration);

            as.play(oaWidth)
              .with(oaHeight)
              .with(oaX)
              .with(oaY);
        }

    }

    private boolean show = false;

    public void start(View v)
    {
        if (!false)
        {
            as.start();
        }

    }

}
