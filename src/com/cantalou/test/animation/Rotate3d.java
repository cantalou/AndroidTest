package com.cantalou.test.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3d extends Animation
{
    private float mFromDegree;
    private float mToDegree;
    private float mCenterX;
    private float mCenterY;
    private Camera mCamera;

    public Rotate3d(float fromDegree, float toDegree, float left, float top, float centerX, float centerY)
    {
        this.mFromDegree = fromDegree;
        this.mToDegree = toDegree;
        this.mCenterX = centerX;
        this.mCenterY = centerY;

    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        float degrees = mFromDegree + (mToDegree - mFromDegree) * interpolatedTime;
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Matrix matrix = t.getMatrix();

        mCamera.save();
        mCamera.translate(0, 0, 8000 * interpolatedTime);
        mCamera.rotateY(degrees);
        mCamera.getMatrix(matrix);
        mCamera.restore();


        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
