package com.wy.test.skin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.wy.test.R;
import com.wy.test.util.PrefUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import static com.wy.test.util.FileUtil.*;
import static com.wy.test.util.ReflectionUtil.*;

public class SkinActivity extends Activity implements OnClickListener {

	private SkinManager skinManager = SkinManager.getInstance();

	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_skin);
		initView();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		skinManager.changeResources(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin, menu);
		return true;
	}

	private void initView() {
		CheckBox cb = (CheckBox) findViewById(R.id.toggleResources);
		if (cb != null) {
			cb.setChecked(!TextUtils.isEmpty(sp.getString("skinPath", "")));
			cb.setOnClickListener(this);
		}

		cb = (CheckBox) findViewById(R.id.toggleNight);
		if (cb != null) {
			cb.setChecked(sp.getBoolean("night", false));
			cb.setOnClickListener(this);
		}

		View v = findViewById(R.id.next);
		if (v != null) {
			v.setOnClickListener(this);
		}

		v = findViewById(R.id.tt);
	}

	public void next(View v) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggleResources: {
			PrefUtil.set(this, "skinPath", TextUtils.isEmpty(sp.getString("skinPath", "")) ? "skinPath" : "");
			skinManager.toggle(this);
			setContentView(R.layout.activity_skin);
			initView();
			break;
		}

		case R.id.toggleNight: {
			sp.edit().putBoolean("night", !sp.getBoolean("night", false)).commit();
			skinManager.toggle(this);
			setContentView(R.layout.activity_skin);
			initView();
			break;
		}

		case R.id.next: {
			Intent i = new Intent(this, SkinTwoActivity.class);
			startActivity(i);
			break;
		}

		default:
			break;
		}

	}

	public void toggle(View v) {
		skinManager.toggle(this);
		setContentView(R.layout.activity_skin);
		initView();
	}
}
