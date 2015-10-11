package com.wy.test.instrumentation;

import com.wy.test.skin.SkinManager;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

public class WyInstrumentation extends Instrumentation {

	private static final String TAG = "WyInstrumentation";

	private SkinManager mSkinManager = SkinManager.getInstance();

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
		Log.d(TAG, "callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) ");
		mSkinManager.changeResources(activity);
		super.callActivityOnCreate(activity, icicle, persistentState);
	}

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle) {
		Log.d(TAG, "callActivityOnCreate(Activity activity, Bundle icicle) ");
		mSkinManager.changeResources(activity);
		super.callActivityOnCreate(activity, icicle);
	}

}
