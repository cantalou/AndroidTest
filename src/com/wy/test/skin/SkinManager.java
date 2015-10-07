package com.wy.test.skin;

import java.io.File;

import com.wy.test.util.PrefUtil;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
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

	private Resources createSkinResource(Activity activity, String path) throws Exception {
		if (skinResources == null) {
			String skinDir = activity.getFilesDir().getAbsolutePath();
			File f = new File(skinDir, SKIN_FILE_NAME);
			if (!f.exists()) {
				copyAssetsFile(activity, SKIN_FILE_NAME, skinDir, SKIN_FILE_NAME);
			}

			AssetManager am = AssetManager.class.newInstance();
			invoke(am, "addAssetPath", new Class<?>[] { String.class }, f.getAbsolutePath());
			skinResources = new SkinResources(am, defaultResources);
		}
		return skinResources;
	}

	private Resources createProxyResource(Activity activity, String path) throws Exception {
		if (proxyResources == null) {
			if (defaultResources == null) {
				defaultResources = activity.getResources();
			}
			proxyResources = new ProxyResources(activity, createSkinResource(activity, path), defaultResources);
		}
		return proxyResources;
	}

	public void toggle(Activity activity) {
		String skinPath = PrefUtil.get(activity, "skinPath");
		PrefUtil.set(activity, "skinPath", TextUtils.isEmpty(skinPath) ? "skinPath" : "");
		changeResources(activity);
	}

	public void changeResources(Activity activity) {

		try {
			String skinPath = get(activity, "skinPath");

			if (defaultResources == null) {
				defaultResources = activity.getResources();
			}

			Resources res = null;
			if (TextUtils.isEmpty(skinPath)) {
				res = defaultResources;
			} else {
				res = createProxyResource(activity, skinPath);
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
