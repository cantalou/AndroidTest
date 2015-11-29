package com.cantalou.test.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class DrawRingActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RingView rv = new RingView(this);
        rv.setLayoutParams(new LayoutParams(100, 100));

        RingView1 rv1 = new RingView1(this);
        rv1.setLayoutParams(new LayoutParams(50, 50));

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(rv);
        ll.addView(rv1);

        setContentView(ll);
    }

    public static class RingView extends View
    {

        private final Paint paint;
        private final Context context;

        public RingView(Context context)
        {
            this(context, null);
        }

        public RingView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            this.context = context;
            this.paint = new Paint();
            this.paint.setAntiAlias(true); // 消除锯齿
            this.paint.setStyle(Paint.Style.STROKE); // 绘制空心圆
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int innerCircle = dip2px(context, 83); // 设置内圆半径
            int ringWidth = dip2px(context, 5); // 设置圆环宽度

            // 绘制内圆
            this.paint.setARGB(155, 167, 190, 206);
            this.paint.setStrokeWidth(50);
            canvas.drawCircle(centerX, centerY, 40, this.paint);

            //			// 绘制圆环
            //			this.paint.setARGB(255, 212, 225, 233);
            //			this.paint.setStrokeWidth(ringWidth);
            //			canvas.drawCircle(center, center, innerCircle + 1 + ringWidth / 2, this.paint);
            //
            //			// 绘制外圆
            //			this.paint.setARGB(155, 167, 190, 206);
            //			this.paint.setStrokeWidth(2);
            //			canvas.drawCircle(center, center, innerCircle + ringWidth, this.paint);

            super.onDraw(canvas);
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        public static int dip2px(Context context, float dpValue)
        {
            final float scale = context.getResources()
                                       .getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }

    public static class RingView1 extends View
    {

        private final Paint paint;
        private final Context context;

        public RingView1(Context context)
        {
            this(context, null);
        }

        public RingView1(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            this.context = context;
            this.paint = new Paint();
            this.paint.setAntiAlias(true); // 消除锯齿
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int innerCircle = dip2px(context, 83); // 设置内圆半径
            int ringWidth = dip2px(context, 5); // 设置圆环宽度

            // 绘制内圆
            this.paint.setARGB(155, 167, 190, 206);
            canvas.drawCircle(centerX, centerY, 65, this.paint);

            //			// 绘制圆环
            //			this.paint.setARGB(255, 212, 225, 233);
            //			this.paint.setStrokeWidth(ringWidth);
            //			canvas.drawCircle(center, center, innerCircle + 1 + ringWidth / 2, this.paint);
            //
            //			// 绘制外圆
            //			this.paint.setARGB(155, 167, 190, 206);
            //			this.paint.setStrokeWidth(2);
            //			canvas.drawCircle(center, center, innerCircle + ringWidth, this.paint);

            super.onDraw(canvas);
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        public static int dip2px(Context context, float dpValue)
        {
            final float scale = context.getResources()
                                       .getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }
}
