package com.wy.test.file.copy;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class FileActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        File f = this.getCacheDir();
        Log.i("getAbsolutePath", f.getAbsolutePath());
        Log.i("getPath", f.getPath());
        this.getExternalCacheDir();
        this.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC);

        getSharedPreferences("FileActivity_private", Context.MODE_PRIVATE).edit()
                                                                          .putInt("int", 1)
                                                                          .commit();
        getSharedPreferences("FileActivity_public", Context.MODE_WORLD_READABLE).edit()
                                                                                .putInt("int", 1)
                                                                                .commit();
    }

}
