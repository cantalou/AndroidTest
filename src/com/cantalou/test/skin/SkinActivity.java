package com.cantalou.test.skin;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.cantalou.skin.OnResourcesChangeFinishListener;
import com.cantalou.skin.SkinManager;
import com.cantalou.test.R;
import com.cantalou.android.util.FileUtil;

public class SkinActivity extends Activity implements OnClickListener, OnResourcesChangeFinishListener {

	private SkinManager skinManager = SkinManager.getInstance();

	SharedPreferences sp;

	private String currentSkin = "";

	private CheckBox red, green, night, def;

	private static boolean hasNotCopy = true;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		skinManager.addOnResourcesChangeFinishListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		skinManager.onAttach(this);
		currentSkin = skinManager.getCurrentSkin();
		setContentView(R.layout.activity_skin);
		initView();

		if (hasNotCopy) {
			String dir = getFilesDir().getAbsolutePath() + File.separator;
			FileUtil.copyAssetsFile(this, "green.apk", dir + "green.apk");
			FileUtil.copyAssetsFile(this, "red.apk", dir + "red.apk");
			hasNotCopy = false;
		}

	}

	@Override
	protected void onDestroy() {
		skinManager.onDestroy(this);
		super.onDestroy();
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

		green = (CheckBox) findViewById(R.id.green);
		green.setChecked(currentSkin.endsWith("green.apk"));
		green.setOnClickListener(this);

		night = (CheckBox) findViewById(R.id.night);
		night.setChecked(SkinManager.DEFAULT_SKIN_NIGHT.equals(currentSkin));
		night.setOnClickListener(this);

		def = (CheckBox) findViewById(R.id.def);
		def.setChecked(SkinManager.DEFAULT_SKIN_PATH.equals(currentSkin));
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
		case R.id.green: {
			if (!currentSkin.endsWith("green.apk")) {
				skinManager.changeResources(this, getFilesDir().getAbsolutePath() + "/green.apk");
			}
			break;
		}

		case R.id.night: {
			if (!currentSkin.endsWith(SkinManager.DEFAULT_SKIN_NIGHT)) {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NIGHT);
			}
			break;
		}

		case R.id.def: {
			if (!currentSkin.endsWith(SkinManager.DEFAULT_SKIN_PATH)) {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_PATH);
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
			green.setChecked(false);
			night.setChecked(false);
			currentSkin = skinManager.getCurrentSkin();
			if (currentSkin.endsWith("red.apk")) {
				red.setChecked(true);
			} else if (currentSkin.endsWith("green.apk")) {
				green.setChecked(true);
			} else if (currentSkin.endsWith(SkinManager.DEFAULT_SKIN_NIGHT)) {
				night.setChecked(true);
			} else {
				def.setChecked(true);
			}
		}
	}

}
