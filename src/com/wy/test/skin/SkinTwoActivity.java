package com.wy.test.skin;

import com.wy.test.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class SkinTwoActivity extends Activity {

	private SkinManager skinManager = SkinManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		skinManager.changeResources(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin, menu);
		return true;
	}

	public void toggle(View v) {
		skinManager.toggle(this);
		setContentView(R.layout.activity_skin);
	}

	public void next(View v) {
		Intent i = new Intent(this, SkinTwoActivity.class);
		startActivity(i);
	}
}
