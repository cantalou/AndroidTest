package com.wy.test.skin;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import com.wy.test.util.ReflectionUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class ProxyDefaultResources extends ProxyResources {

	private static final String TAG = "ProxyDefaultResources";

	/**
	 * 资源缓存
	 */
	private LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();

	private LongSparseArray<WeakReference<Drawable.ConstantState>> colorDrawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();

	private SparseArray<WeakReference<ColorStateList>> colorStateListCache = new SparseArray<WeakReference<ColorStateList>>();

	private Resources defaultResources;

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param skinRes
	 *            skin resources
	 * @param defRes
	 *            default resources
	 * @param nightMode
	 *            夜间模式
	 */
	public ProxyDefaultResources(Context cxt, Resources skinRes, Resources defRes, boolean nightMode) {
		super(cxt, skinRes, defRes, nightMode);
		defaultResources = new ProxyResources(cxt, skinRes, defRes);
	}

	static Class<?>[] loadParamType = new Class<?>[] { TypedValue.class, int.class };

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		String log = "loadDrawable value:" + value + (value.string == null ? ", name:" + getResourceName(id) : "") + ",id:" + toHex(id);

		long key = (((long) value.assetCookie) << 32) | value.data;
		boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
		LongSparseArray<WeakReference<ConstantState>> cache = isColorDrawable ? colorDrawableCache : drawableCache;

		Drawable result = getCachedDrawable(cache, key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}
		boolean isFromSkinResources = false;
		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		} else if (notFoundedSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.v(TAG, log + "notFoundInSkinIds contain ");
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, log + " convertId not found ");
				result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			} else {
				Resources res = skinResources;
				if (isColorDrawable) {
					res.getValue(skinId, value, true);
				}
				result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
				// 当同一张图片放在app资源和皮肤资源的不同分辨率目录下, 使用皮肤资源id获取文件名称, 再次尝试
				if (result == null) {
					Log.v(TAG, "skinResources.loadDrawable() return null ,use getValue(skinId,value,true) try again");
					res.getValue(skinId, value, true);
					result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
				}
				Object resultInfo = result instanceof ColorDrawable ? toHex((Integer) ReflectionUtil.getValue(
						ReflectionUtil.getValue(result, "mState"), "mUseColor")) : result;
				Log.v(TAG, log + ", skin id:" + toHex(skinId) + ",result:" + resultInfo + ", from skinResources ");
				isFromSkinResources = true;
			}
		}
		if (result == null && isFromSkinResources) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		}

		if (result != null) {
			synchronized (this) {
				cache.put(key, new WeakReference<ConstantState>(result.getConstantState()));
			}
		}

		return result;
	}

	ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		String log = "loadColorStateList ,value:" + value + (value.string == null ? ", name:" + getResourceName(id) : "") + ",id:"
				+ toHex(id);

		int key = (value.assetCookie << 24) | value.data;

		ColorStateList result = getCachedColorStateList(key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}

		boolean isFromSkinResources = false;
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			// 系统资源
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else if (notFoundedSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.v(TAG, log + " notFoundInSkinIds contain ");
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, log + "convertId not found ");
				result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			} else {
				Resources res = skinResources;
				if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
					res.getValue(skinId, value, true);
				}
				result = (ColorStateList) ReflectionUtil.invoke(res, "loadColorStateList", loadParamType, value, id);
				Log.v(TAG, log + ", skin id:" + toHex(skinId) + ",result:" + result + ", from resources :" + res + "");
				isFromSkinResources = true;
			}
		}
		if (result == null && isFromSkinResources) {
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		}

		if (result != null) {
			synchronized (this) {
				colorStateListCache.put(key, new WeakReference<ColorStateList>(result));
			}
		}
		return result;
	}

	protected synchronized Drawable getCachedDrawable(LongSparseArray<WeakReference<ConstantState>> cache, long key) {
		WeakReference<ConstantState> wr = cache.get(key);
		if (wr != null) { // we have the key
			Drawable.ConstantState entry = wr.get();
			if (entry != null) {
				return entry.newDrawable(this);
			} else { // our entry has been purged
				cache.delete(key);
			}
		}
		return null;
	}

	protected synchronized ColorStateList getCachedColorStateList(int key) {
		WeakReference<ColorStateList> wr = colorStateListCache.get(key);
		if (wr != null) { // we have the key
			ColorStateList entry = wr.get();
			if (entry != null) {
				return entry;
			} else { // our entry has been purged
				colorStateListCache.delete(key);
			}
		}
		return null;
	}

	public synchronized void clearCache() {
		super.clearCache();
		drawableCache.clear();
		colorDrawableCache.clear();
		colorStateListCache.clear();
	}
}
