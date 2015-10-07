package com.wy.test.imageloader;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.os.Bundle;

public class UniverdalImageLoader extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ImageLoaderConfiguration cfg = new ImageLoaderConfiguration.Builder(this).diskCache(null)
                                                                                 .build();

    }
}
