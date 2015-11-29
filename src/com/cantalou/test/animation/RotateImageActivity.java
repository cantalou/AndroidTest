package com.cantalou.test.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;

public final class RotateImageActivity extends Activity
{

    private RotateImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.rotate_image);
        view = (RotateImageView) findViewById(id.rotateImage);
    }

    public void add(View v)
    {
        view.addX(10);
        view.invalidate();
    }

}
