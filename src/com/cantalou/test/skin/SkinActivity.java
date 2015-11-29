package com.cantalou.test.skin;

import java.io.File;

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

import com.cantalou.skin.SkinManager;
import com.cantalou.test.R;
import com.cantalou.android.util.FileUtil;
import com.cantalou.android.util.PrefUtil;

public class SkinActivity extends Activity implements OnClickListener {

	private SkinManager skinManager = SkinManager.getInstance();

	SharedPreferences sp;

	private CheckBox red, green, night, def;

	private String currentSkin;

	private static boolean hasNotCopy = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSkin = PrefUtil.getString(this, SkinManager.PREF_KEY_SKIN_NAME);
		if (currentSkin == null) {
			currentSkin = "def";
		}
		setContentView(R.layout.activity_skin);
		initView();

		if (hasNotCopy) {
			String dir = skinManager.getSkinDir(this).getAbsolutePath() + File.separator;
			FileUtil.copyAssetsFile(this, "green.apk", dir + "green.apk");
			FileUtil.copyAssetsFile(this, "red.apk", dir + "red.apk");
			hasNotCopy = false;
		}

	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		skinManager.onAttach(this);
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
		red.setChecked("red.apk".equals(currentSkin));
		red.setOnClickListener(this);

		green = (CheckBox) findViewById(R.id.green);
		green.setChecked("green.apk".equals(currentSkin));
		green.setOnClickListener(this);

		night = (CheckBox) findViewById(R.id.night);
		night.setChecked(SkinManager.DEFAULT_SKIN_NAME_NIGHT.equals(currentSkin));
		night.setOnClickListener(this);

		def = (CheckBox) findViewById(R.id.def);
		def.setChecked(SkinManager.DEFAULT_SKIN_NAME.equals(currentSkin));
		def.setOnClickListener(this);

		findViewById(R.id.next).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.next: {
			startActivity(new Intent(this, SkinTwoActivity.class));
			break;
		}

		case R.id.red: {
			if (!currentSkin.equals("red")) {
				skinManager.changeResources(this, "red.apk");
				red.setChecked(true);
				def.setChecked(false);
				currentSkin = "red";
			} else {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME);
				red.setChecked(false);
				def.setChecked(true);
				currentSkin = "def";
			}
			green.setChecked(false);
			night.setChecked(false);
			break;
		}

		case R.id.green: {
			if (!currentSkin.equals("green")) {
				skinManager.changeResources(this, "green.apk");
				green.setChecked(true);
				def.setChecked(false);
				currentSkin = "green";
			} else {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME);
				green.setChecked(false);
				def.setChecked(true);
				currentSkin = "def";
			}
			red.setChecked(false);
			night.setChecked(false);
			break;
		}

		case R.id.night: {
			if (!currentSkin.equals("night")) {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME_NIGHT);
				night.setChecked(true);
				def.setChecked(false);
				currentSkin = "night";
			} else {
				skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME);
				night.setChecked(false);
				def.setChecked(true);
				currentSkin = "def";
			}
			red.setChecked(false);
			green.setChecked(false);
			break;
		}

		case R.id.def: {
			skinManager.changeResources(this, SkinManager.DEFAULT_SKIN_NAME);
			def.setChecked(true);
			red.setChecked(false);
			green.setChecked(false);
			night.setChecked(false);
			currentSkin = "def";
			break;
		}

		default:
			break;
		}

	}
}
