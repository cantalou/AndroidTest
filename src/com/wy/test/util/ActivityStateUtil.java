package com.wy.test.util;

import android.app.Activity;

public class ActivityStateUtil {

	public static boolean isDestroy(Activity activity) {
		return activity == null || activity.isFinishing();
	}
}
