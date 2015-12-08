package com.cantalou.test.ui;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;

public class DrawRingArcActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final View v = new RingView(this);
        setContentView(v);


        v.postDelayed(new Runnable()
        {

            public void run()
            {
                ObjectAnimator fromAngle = ObjectAnimator.ofInt(v, "fromAngle", 0, 360);
                ObjectAnimator rangeAngle = ObjectAnimator.ofInt(v, "rangeAngle", 0, 360);
                ObjectAnimator rangeAngle2 = ObjectAnimator.ofInt(v, "rangeAngle", 360, 0);
                AnimatorSet as = new AnimatorSet();
                as.play(rangeAngle2)
                  .with(fromAngle)
                  .after(rangeAngle);
                as.setInterpolator(new CycleInterpolator(1));
                as.setDuration(6000);
                as.start();
            }
        }, 200);

    }

    public static class RingView extends View
    {

        private int fromAngle = 0;

        private int rangeAngle = 0;

        private final Paint paint;

        public RingView(Context context)
        {
            this(context, null);
        }

        public RingView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            this.paint = new Paint();
            this.paint.setAntiAlias(true); // 消除锯齿
            paint.setStyle(Paint.Style.STROKE);//
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // 绘制外圆
            paint.setARGB(255, 127, 255, 212);
            paint.setStrokeWidth(10);
            canvas.drawCircle(centerX, centerY, 60, paint);

            // 绘制内圆
            paint.setARGB(100, 155, 0, 212);
            RectF rf = new RectF(centerX - 60, centerY - 60, centerX + 60, centerY + 60);
            canvas.drawArc(rf, fromAngle, rangeAngle, false, paint);

            super.onDraw(canvas);
        }

        public int getFromAngle()
        {
            return fromAngle;
        }

        public void setFromAngle(int fromAngle)
        {
            this.fromAngle = fromAngle;
            this.invalidate();
        }

        public int getRangeAngle()
        {
            return rangeAngle;
        }

        public void setRangeAngle(int rangeAngle)
        {
            this.rangeAngle = rangeAngle;
            this.invalidate();
        }

    }

}
