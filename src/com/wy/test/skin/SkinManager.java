package com.wy.test.skin;

import java.io.File;

import com.wy.test.util.PrefUtil;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import static com.wy.test.util.ReflectionUtil.*;
import static com.wy.test.util.FileUtil.copyAssetsFile;
import static com.wy.test.util.PrefUtil.*;

public class SkinManager {

	private static final String TAG = "SkinManager";

	private static final String SKIN_FILE_NAME = "skin.apk";

	/**
	 * 皮肤资源
	 */
	private Resources skinResources;

	/**
	 * 默认资源
	 */
	private Resources defaultResources;

	/**
	 * 代理资源
	 */
	private Resources proxyResources;

	private static class InstanceHolder {
		static final SkinManager INSTANCE = new SkinManager();
	}

	private SkinManager() {
	}

	public static SkinManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private Resources createSkinResource(Activity activity, String path, boolean night) throws Exception {

		Resources skinRsources = null;
		if (!TextUtils.isEmpty(path)) {
			String skinDir = activity.getFilesDir().getAbsolutePath();
			File f = new File(skinDir, SKIN_FILE_NAME);
			if (!f.exists()) {
				copyAssetsFile(activity, SKIN_FILE_NAME, skinDir, SKIN_FILE_NAME);
			}

			AssetManager am = AssetManager.class.newInstance();
			invoke(am, "addAssetPath", new Class<?>[] { String.class }, f.getAbsolutePath());
			skinRsources = new SkinResources(am, defaultResources);
		}

		//skinResources = new ProxyResources(activity, skinRsources, defaultResources, night);
		return skinRsources;
	}

	// private Resources createProxySkinResource(Activity activity, String path)
	// throws Exception {
	// if (proxyResources == null) {
	// if (defaultResources == null) {
	// defaultResources = activity.getResources();
	// }
	// proxyResources = new ProxySkinResources(activity,
	// createSkinResource(activity, path), defaultResources);
	// }
	// return proxyResources;
	// }

	private Resources createProxyDefaultResource(Activity activity, String path, boolean night) throws Exception {
		if (defaultResources == null) {
			defaultResources = activity.getResources();
		}
		proxyResources = new ProxyDefaultResources(activity, createSkinResource(activity, path, night), defaultResources, night);
		return proxyResources;
	}

	public void toggle(Activity activity) {
		changeResources(activity);
	}

	public void changeResources(Activity activity) {

		try {
			String skinPath = get(activity, "skinPath");
			boolean night = getBoolean(activity, "night");
			Log.d(TAG, "skinPath:" + skinPath + ",night:" + night);

			if (defaultResources == null) {
				defaultResources = activity.getResources();
			}

			Resources res = null;
			if (TextUtils.isEmpty(skinPath) && !night) {
				res = defaultResources;
			} else {
				res = createProxyDefaultResource(activity, skinPath, night);
			}

			if (activity.getResources() == res) {
				return;
			}

			// ContextThemeWrapper add mResources field in JELLY_BEAN
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
				setValue(activity, "mResources", res);
			} else {
				setValue(activity.getBaseContext(), "mResources", res);
			}
			setValue(activity, "mTheme", null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
