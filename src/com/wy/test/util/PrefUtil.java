package com.wy.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtil {

	public static void set(Context cxt, String key, String value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
		sp.edit().putString(key, value).apply();
	}
	
	public static String get(Context cxt, String key) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
		return sp.getString(key, null);
	}
}
