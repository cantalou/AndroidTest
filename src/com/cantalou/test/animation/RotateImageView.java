package com.cantalou.test.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.cantalou.test.R;

public class RotateImageView extends ImageView
{

    private Bitmap showBmp;
    private Matrix matrix; // 作用矩阵
    private Camera camera;
    private int deltaX = 300, deltaY; // 翻转角度差值
    private int centerX, centerY; // 图片中心点

    public RotateImageView(Context context)
    {
        super(context);
        initData();
    }

    public RotateImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initData();
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initData();
    }

    private void initData()
    {
        showBmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.f);
        centerX = showBmp.getWidth() / 2;
        centerY = showBmp.getHeight() / 2;
        matrix = new Matrix();
        camera = new Camera();

    }

    int lastMouseX;
    int lastMouseY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.i("", "event.getY():" + y + "lastMouseY : " + lastMouseY);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastMouseX = x;
                lastMouseY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (x != lastMouseX)
                {
                    deltaX += x - lastMouseX;
                }
                if (y != lastMouseY)
                {
                    deltaY += y - lastMouseY;
                }

                lastMouseX = x;
                lastMouseY = y;

                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        camera.save();

        // 绕X轴翻转
        camera.rotateX(-deltaY);

        // 绕Y轴翻转
        camera.rotateY(deltaX);

        // 设置camera作用矩阵
        camera.getMatrix(matrix);
        camera.restore();

        // 设置翻转中心点
        matrix.preTranslate(-this.centerX, -this.centerY);
        matrix.postTranslate(this.centerX, this.centerY);

        canvas.drawBitmap(showBmp, matrix, null);
    }

    public void addY(int deltaY1)
    {
        this.deltaY += deltaY1;
    }

    public void addX(int deltaX1)
    {
        this.deltaX += deltaX1;
    }

}
