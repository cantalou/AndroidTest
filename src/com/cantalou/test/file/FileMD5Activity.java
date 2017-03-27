package com.cantalou.test.file;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.cantalou.test.R;

public class FileMD5Activity extends Activity {

    static {
        System.load("m4399");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_md5);


    }
}
