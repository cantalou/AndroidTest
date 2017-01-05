package com.cantalou.test.skin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.cantalou.android.util.FileUtil;
import com.cantalou.skin.OnResourcesChangeFinishListener;
import com.cantalou.skin.ResourcesManager;
import com.cantalou.skin.SkinManager;
import com.cantalou.test.R;

import java.io.File;

public class SkinActivity extends FragmentActivity implements OnClickListener, OnResourcesChangeFinishListener {

    private SkinManager skinManager = SkinManager.getInstance();

    SharedPreferences sp;

    private String currentSkin = "";

    private CheckBox red, def;

    private static boolean hasNotCopy = true;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        skinManager.addOnResourcesChangeFinishListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentSkin = skinManager.getCurrentSkin();
        setContentView(R.layout.activity_skin);
        initView();

        if (hasNotCopy) {
            String dir = getFilesDir().getAbsolutePath() + File.separator;
            FileUtil.copyAssetsFile(this, "red.apk", dir + "red.apk");
            hasNotCopy = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.skin, menu);
        return true;
    }

    private void initView() {

        red = (CheckBox) findViewById(R.id.red);
        red.setChecked(currentSkin.endsWith("red.apk"));
        red.setOnClickListener(this);

        def = (CheckBox) findViewById(R.id.def);
        def.setChecked(currentSkin.endsWith(ResourcesManager.DEFAULT_RESOURCES));
        def.setOnClickListener(this);

        findViewById(R.id.activity).setOnClickListener(this);
        findViewById(R.id.fragment).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.activity: {
                startActivity(new Intent(this, SkinTwoActivity.class));
                break;
            }

            case R.id.fragment: {
                startActivity(new Intent(this, SkinFragmentActivity.class));
                break;
            }

            case R.id.red: {
                if (!currentSkin.endsWith("red.apk")) {
                    skinManager.changeResources(this, getFilesDir().getAbsolutePath() + "/red.apk");
                }
                break;
            }

            case R.id.def: {
                if (!currentSkin.endsWith(ResourcesManager.DEFAULT_RESOURCES)) {
                    skinManager.changeResources(this, ResourcesManager.DEFAULT_RESOURCES);
                }
                break;
            }

            default:
                break;
        }
    }

    @Override
    public void onResourcesChangeFinish(boolean success) {
        if (success) {
            def.setChecked(false);
            red.setChecked(false);
            currentSkin = skinManager.getCurrentSkin();
            if (currentSkin.endsWith("red.apk")) {
                red.setChecked(true);
            } else {
                def.setChecked(true);
            }
        }
    }

}
