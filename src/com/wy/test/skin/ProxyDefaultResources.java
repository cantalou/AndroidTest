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

public class ProxyDefaultResources extends Resources {

	private static final String TAG = "ProxyDefaultResources";

	/**
	 * app资源id前缀
	 */
	private static final int APP_ID_MASK = 0x7F000000;

	/**
	 * 皮肤资源id映射
	 */
	private SparseIntArray skinIdMap = new SparseIntArray();
	
	/**
	 * 夜间模式资源id映射
	 */
	private SparseIntArray nightIdMap = new SparseIntArray();

	/**
	 * 皮肤资源不存在的id
	 */
	private SparseIntArray notFoundInSkinIds = new SparseIntArray();

	/**
	 * 皮肤资源
	 */
	private Resources skinResources;

	/**
	 * 默认资源
	 */
	private Resources defaultResources;

	/**
	 * 应用包名
	 */
	private String packageName;

	/**
	 * 资源名称缓存数量
	 */
	private static final int resourceNameCacheSize = 31;

	/**
	 * 资源名称缓存id
	 */
	private int[] resourceNameIdCache = new int[resourceNameCacheSize];

	/**
	 * 资源名称缓存
	 */
	private String[] resourceNameCache = new String[resourceNameCacheSize];

	/**
	 * 资源缓存
	 */
	private LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();
	private LongSparseArray<WeakReference<Drawable.ConstantState>> colorDrawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();
	private SparseArray<WeakReference<ColorStateList>> colorStateListCache = new SparseArray<WeakReference<ColorStateList>>();

	/**
	 * 是否夜间模式
	 */
	private boolean nightMode = false;

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
		this(cxt, skinRes, defRes);
		this.nightMode = nightMode;
	}

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param assets
	 *            Previously created AssetManager.
	 * @param metrics
	 *            Current display metrics to consider when selecting/computing
	 *            resource values.
	 * @param config
	 *            Desired device configuration to consider when
	 */
	public ProxyDefaultResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
		super(assets, metrics, config);
	}

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param skinRes
	 *            skin resources
	 * @param defRes
	 *            default resources
	 */
	public ProxyDefaultResources(Context cxt, Resources skinRes, Resources defRes) {
		super(defRes.getAssets(), defRes.getDisplayMetrics(), defRes.getConfiguration());
		skinResources = skinRes;
		defaultResources = defRes;
		packageName = cxt.getPackageName();
	}

	/**
	 * 将应用资源id转成皮肤资源id
	 * 
	 * @param id
	 * @return 皮肤资源id
	 */
	public int toSkinId(int id) {

		if (id == 0 || skinResources == null) {
			return 0;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回0
		if (notFoundInSkinIds.get(id) > 0) {
			return 0;
		}

		int skinId = skinIdMap.get(id);
		if (skinId != 0) {
			return skinId;
		}

		String name = getResourceName(id);
		if (TextUtils.isEmpty(name)) {
			return 0;
		}

		skinId = skinResources.getIdentifier(name, null, packageName);
		if (skinId == 0) {
			notFoundInSkinIds.put(id, id);
		} else {
			skinIdMap.put(id, skinId);
		}
		return skinId;
	}
	
	/**
	 * 将应用资源id转成夜间模式资源id
	 * 
	 * @param id
	 * @return 夜间模式资源id
	 */
	public int toNightId(int id) {

		if (id == 0 || skinResources == null) {
			return 0;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回0
		if (notFoundInSkinIds.get(id) > 0) {
			return 0;
		}

		int skinId = skinIdMap.get(id);
		if (skinId != 0) {
			return skinId;
		}

		String name = getResourceName(id);
		if (TextUtils.isEmpty(name)) {
			return 0;
		}

		skinId = skinResources.getIdentifier(name, null, packageName);
		if (skinId == 0) {
			notFoundInSkinIds.put(id, id);
		} else {
			skinIdMap.put(id, skinId);
		}
		return skinId;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValue(skinId, outValue, resolveRefs);
			Log.v(TAG, "getValue(int id, TypedValue outValue, boolean resolveRefs) skin id:" + toHex(skinId) + ", value:" + outValue);
		} else {
			super.getValue(id, outValue, resolveRefs);
		}
		if (nightMode && outValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && outValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
			
		}
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValueForDensity(skinId, density, outValue, resolveRefs);
			Log.v(TAG, "getValueForDensity skin id:" + toHex(skinId) + ", value:" + outValue);
		} else {
			super.getValueForDensity(id, density, outValue, resolveRefs);
		}
	}

	private String toHex(int id) {
		return "0x" + Integer.toHexString(id);
	}

	static Class<?>[] loadParamType = new Class<?>[] { TypedValue.class, int.class };

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		Log.v(TAG, "loadDrawable ,value:" + value + (value.string == null ? ", name:" + getResourceName(id) : "") + ",id:" + toHex(id));

		long key = (((long) value.assetCookie) << 32) | value.data;
		boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
		LongSparseArray<WeakReference<ConstantState>> cache = isColorDrawable ? colorDrawableCache : drawableCache;

		Drawable result = getCachedDrawable(cache, key);
		if (result != null) {
			Log.v(TAG, "from cache \n");
			return result;
		}
		boolean isFromSkinResources = false;
		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		} else if (notFoundInSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.v(TAG, "notFoundInSkinIds contain \n");
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, "convertId not found \n");
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
				Log.v(TAG, ", skin id:" + toHex(skinId) + ",result:" + resultInfo + ", from skinResources ");
				isFromSkinResources = true;
			}
		}
		if (result == null && isFromSkinResources) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		}

		if (result != null) {
			synchronized (cache) {
				cache.put(key, new WeakReference<ConstantState>(result.getConstantState()));
			}
		}

		return result;
	}

	ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		Log.v(TAG, "loadColorStateList ,value:" + value + (value.string == null ? ", name:" + getResourceName(id) : "") + ",id:"
				+ toHex(id));

		int key = (value.assetCookie << 24) | value.data;

		ColorStateList result = getCachedColorStateList(key);
		if (result != null) {
			Log.v(TAG, "from cache \n");
			return result;
		}

		boolean isFromSkinResources = false;
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			// 系统资源
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else if (notFoundInSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.v(TAG, "notFoundInSkinIds contain \n");
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, "convertId not found \n");
				result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			} else {
				Resources res = skinResources;
				if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
					res.getValue(skinId, value, true);
				}
				result = (ColorStateList) ReflectionUtil.invoke(res, "loadColorStateList", loadParamType, value, id);
				Log.v(TAG, ", skin id:" + toHex(skinId) + ",result:" + result + ", from resources :" + res + "\n");
				isFromSkinResources = true;
			}
		}
		if (result == null && isFromSkinResources) {
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		}

		if (result != null) {
			synchronized (colorStateListCache) {
				colorStateListCache.put(key, new WeakReference<ColorStateList>(result));
			}
		}
		return result;
	}

	@Override
	public String getResourceName(int resid) throws NotFoundException {
		if (resid == 0) {
			return "";
		}

		int index = resid & resourceNameCacheSize;
		if (resourceNameIdCache[index] == resid) {
			return resourceNameCache[index];
		}

		try {
			String name = super.getResourceName(resid);
			resourceNameCache[index] = name;
			return name;
		} catch (Exception e) {
			return null;
		}
	}

	private synchronized Drawable getCachedDrawable(LongSparseArray<WeakReference<ConstantState>> cache, long key) {
		synchronized (cache) {
			WeakReference<ConstantState> wr = cache.get(key);
			if (wr != null) { // we have the key
				Drawable.ConstantState entry = wr.get();
				if (entry != null) {
					return entry.newDrawable(this);
				} else { // our entry has been purged
					cache.delete(key);
				}
			}
		}
		return null;
	}

	private ColorStateList getCachedColorStateList(int key) {
		synchronized (colorStateListCache) {
			WeakReference<ColorStateList> wr = colorStateListCache.get(key);
			if (wr != null) { // we have the key
				ColorStateList entry = wr.get();
				if (entry != null) {
					return entry;
				} else { // our entry has been purged
					colorStateListCache.delete(key);
				}
			}
		}
		return null;
	}
}
