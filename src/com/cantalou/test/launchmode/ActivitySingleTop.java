package com.cantalou.test.launchmode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.cantalou.test.R;

/**
 * @author Lin Zhiwei
 * @date 16-10-11 下午10:43
 */
public class ActivitySingleTop extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
    }

    public void startSingleTop(View v) {
        Intent intent = new Intent(this, getClass());
        startActivity(intent);
    }

    public void startSingleTopWithForResult(View v) {
        Intent intent = new Intent(this, getClass());
        startActivityForResult(intent, 100);
    }

}
