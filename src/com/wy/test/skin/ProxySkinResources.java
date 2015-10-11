package com.wy.test.skin;

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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class ProxySkinResources extends Resources {

	private static final String TAG = "ProxySkinResources";

	/**
	 * app资源id前缀
	 */
	private static final int APP_ID_MASK = 0x7F000000;

	/**
	 * 资源id映射
	 */
	private SparseIntArray idMap = new SparseIntArray();

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
	public ProxySkinResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
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
	public ProxySkinResources(Context cxt, Resources skinRes, Resources defRes) {
		super(skinRes.getAssets(), defRes.getDisplayMetrics(), defRes.getConfiguration());
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
	public int convertId(int id) {

		if (id == 0) {
			return 0;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回0
		if (notFoundInSkinIds.get(id) > 0) {
			return 0;
		}

		int skinId = idMap.get(id);
		if (skinId != 0) {
			return skinId;
		}
		skinId = skinResources.getIdentifier(defaultResources.getResourceName(id), null, packageName);
		if (skinId == 0) {
			notFoundInSkinIds.put(id, id);
		} else {
			idMap.put(id, skinId);
		}
		return skinId;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		int skinId = convertId(id);
		if (skinId != 0) {
			skinResources.getValue(skinId, outValue, resolveRefs);
			Log.d(TAG, "getValue(int id, TypedValue outValue, boolean resolveRefs) skin id:" + toHex(skinId) + ", value:" + outValue);
		} else {
			defaultResources.getValue(id, outValue, resolveRefs);
		}
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		int skinId = convertId(id);
		if (skinId != 0) {
			skinResources.getValueForDensity(skinId, density, outValue, resolveRefs);
			Log.d(TAG, "getValueForDensity skin id:" + toHex(skinId) + ", value:" + outValue);
		} else {
			defaultResources.getValueForDensity(id, density, outValue, resolveRefs);
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

		Drawable result = null;
		boolean isFromDefaultResources = false;
		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			isFromDefaultResources = true;
		} else if (notFoundInSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.d(TAG, "notFoundInSkinIds contain value:" + value + value.string == null ? ", name:" + getResourceName(id) : "");
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			isFromDefaultResources = true;
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = convertId(id);
			if (skinId == 0) {
				Log.d(TAG, "convertId not found value:" + value);
				result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
				isFromDefaultResources = true;
			} else {
				Resources res = skinResources;
				boolean isColorValue = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
				if (isColorValue && skinId != id) {
					res.getValue(skinId, value, true);
				}
				result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
				// 当同一张图片放在app资源和皮肤资源的不同分辨率目录下, 使用皮肤资源id获取文件名称, 再次尝试
				if (result == null) {
					Log.d(TAG, "loadDrawable(TypedValue value, int id) return null ,use getValue(skinId,value,true) try again");
					res.getValue(skinId, value, true);
					result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
				}
				Object resultInfo = result instanceof ColorDrawable ? toHex((Integer) ReflectionUtil.getValue(
						ReflectionUtil.getValue(result, "mState"), "mUseColor")) : result;
				Log.d(TAG, "loadDrawable(TypedValue value, int id) value:" + value + (isColorValue ? ", name:" + getResourceName(id) : "")
						+ ", skin id:" + toHex(skinId) + ",result:" + resultInfo + ", from resources :" + res);
			}
		}
		if (result == null && !isFromDefaultResources) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		}
		return result;
	}

	ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		ColorStateList result = null;
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			// 系统资源
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else if (notFoundInSkinIds.get(id) > 0) {
			// 皮肤包中不包含id资源
			Log.d(TAG, "notFoundInSkinIds contain value:" + value + value.string == null ? ", name:" + getResourceName(id) : "");
			result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = convertId(id);
			if (skinId == 0) {
				Log.d(TAG, "convertId not found value:" + value);
				result = (ColorStateList) ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			} else {
				Resources res = skinResources;
				result = (ColorStateList) ReflectionUtil.invoke(res, "loadColorStateList", loadParamType, value, id);
				Log.d(TAG, "loadColorStateList(TypedValue value, int id) value:" + value + ", skin id:" + toHex(skinId) + ",result:"
						+ result + ", from resources :" + res);
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

}
