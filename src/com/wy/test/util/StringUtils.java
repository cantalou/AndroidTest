package com.wy.test.util;

import android.text.TextUtils;

public class StringUtils {

	public static boolean isBlank(CharSequence cs) {
		return TextUtils.isEmpty(cs);
	}

	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

}
