package com.wy.test.skin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.wy.test.R;
import com.wy.test.util.FileUtil;
import com.wy.test.util.PrefUtil;

public class SkinActivity extends Activity implements OnClickListener
{

    private SkinManager skinManager = SkinManager.getInstance();

    SharedPreferences sp;

    private CheckBox red, green, night, def;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_skin);
        initView();

        String dir = getFilesDir().getAbsolutePath();
        FileUtil.copyAssetsFile(this, "green.apk", dir, "green.apk");
        FileUtil.copyAssetsFile(this, "red.apk", dir, "red.apk");
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        skinManager.onAttach(this);
    }

    @Override
    protected void onDestroy()
    {
        skinManager.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.skin, menu);
        return true;
    }

    private void initView()
    {
        String currentSkinName = PrefUtil.getString(this, SkinManager.PREF_KEY_SKIN_NAME);

        red = (CheckBox) findViewById(R.id.red);
        red.setChecked("red.apk".equals(currentSkinName));
        red.setOnClickListener(this);

        green = (CheckBox) findViewById(R.id.green);
        green.setChecked("green.apk".equals(currentSkinName));
        green.setOnClickListener(this);

        night = (CheckBox) findViewById(R.id.night);
        night.setChecked(SkinManager.DEFAULT_SKIN_NAME_NIGHT.equals(currentSkinName));
        night.setOnClickListener(this);

        def = (CheckBox) findViewById(R.id.def);
        def.setChecked(SkinManager.DEFAULT_SKIN_NAME.equals(currentSkinName));
        def.setOnClickListener(this);

        findViewById(R.id.next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        red.setChecked(false);
        green.setChecked(false);
        night.setChecked(false);
        def.setChecked(false);
        switch (v.getId())
        {
            case R.id.red:
            {
                skinManager.changeResources(this, "red.apk");
                red.setChecked(true);
                break;
            }

            case R.id.green:
            {
                skinManager.changeResources(this, "green.apk");
                green.setChecked(true);
                break;
            }

            case R.id.night:
            {
                skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME_NIGHT);
                night.setChecked(true);
                break;
            }

            case R.id.def:
            {
                skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME);
                def.setChecked(true);
                break;
            }

            case R.id.next:
            {
                startActivity(new Intent(this, SkinTwoActivity.class));
                break;
            }

            default:
                break;
        }

    }
}
