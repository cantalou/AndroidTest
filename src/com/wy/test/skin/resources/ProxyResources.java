package com.wy.test.skin.resources;

import java.lang.ref.WeakReference;

import com.wy.test.util.ReflectionUtil;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class ProxyResources extends Resources {

	static final String TAG = "ProxyResources";

	/**
	 * app资源id前缀
	 */
	public static final int APP_ID_MASK = 0x7F000000;

	/**
	 * 资源名称缓存数量
	 */
	public static final int RESOURCE_NAME_CACHE_SIZE = 31;

	/**
	 * 皮肤资源id映射
	 */
	protected SparseIntArray skinIdMap = new SparseIntArray();

	/**
	 * 皮肤资源不存在的id
	 */
	protected SparseIntArray notFoundedSkinIds = new SparseIntArray();

	/**
	 * 皮肤资源
	 */
	protected Resources skinResources;

	/**
	 * 默认资源
	 */
	protected Resources defaultResources;

	/**
	 * 应用包名
	 */
	protected String packageName;

	/**
	 * 资源名称缓存id
	 */
	protected int[] resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];

	/**
	 * 资源名称缓存
	 */
	protected String[] resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];

	/**
	 * 资源缓存
	 */
	protected LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();
	protected LongSparseArray<WeakReference<Drawable.ConstantState>> colorDrawableCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>();
	protected SparseArray<WeakReference<ColorStateList>> colorStateListCache = new SparseArray<WeakReference<ColorStateList>>();

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param skinRes
	 *            skin resources
	 * @param defRes
	 *            default resources
	 */
	public ProxyResources(Context cxt, Resources skinRes, Resources defRes) {
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
	public synchronized int toSkinId(int id) {

		if (id == 0 || skinResources == null) {
			return 0;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回0
		if (notFoundedSkinIds.get(id) > 0) {
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
			notFoundedSkinIds.put(id, id);
		} else {
			skinIdMap.put(id, skinId);
		}
		return skinId;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			super.getValue(id, outValue, resolveRefs);
			return;
		}

		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValue(skinId, outValue, resolveRefs);
			outValue.resourceId = -outValue.resourceId;
		} else {
			super.getValue(id, outValue, resolveRefs);
		}
		Log.v(TAG, "getValue " + toString(outValue));
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			super.getValueForDensity(id, density, outValue, resolveRefs);
			return;
		}

		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValueForDensity(skinId, density, outValue, resolveRefs);
			outValue.resourceId = -outValue.resourceId;
		} else {
			super.getValueForDensity(id, density, outValue, resolveRefs);
		}
		Log.v(TAG, "getValueForDensity " + toString(outValue));
	}

	protected String toString(TypedValue value) {
		return " skinId:" + toHex(toSkinId(value.resourceId))
				+ (TextUtils.isEmpty(value.string) ? value + ",name:" + getResourceName(value.resourceId) : value.toString());
	}

	protected String toHex(Object id) {
		if (id == null) {
			return "null";
		}
		if (id instanceof Number) {
			return "0x" + Integer.toHexString(((Number) id).intValue());
		} else {
			return id.toString();
		}

	}

	@Override
	public synchronized String getResourceName(int resid) throws NotFoundException {
		if (resid == 0) {
			return "";
		}

		int index = resid & RESOURCE_NAME_CACHE_SIZE;
		if (resourceNameIdCache[index] == resid) {
			return resourceNameCache[index];
		}

		try {
			String name = super.getResourceName(resid);
			resourceNameIdCache[index] = resid;
			resourceNameCache[index] = name;
			return name;
		} catch (Exception e) {
			return null;
		}
	}

	protected boolean isColor(TypedValue value) {
		return value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
	}

	static Class<?>[] loadParamType = new Class<?>[] { TypedValue.class, int.class };

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		String log = "loadDrawable " + toString(value);

		long key = (((long) value.assetCookie) << 32) | value.data;
		boolean isColor = isColor(value);
		LongSparseArray<WeakReference<ConstantState>> cache = isColor ? colorDrawableCache : drawableCache;

		Drawable result = getCachedDrawable(cache, key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}

		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
		} else if (isColor && value.resourceId < 0) {
			result = (Drawable) ReflectionUtil.invoke(skinResources, "loadDrawable", loadParamType, value, id);
		} else {
			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, log + " convertId not found ");
				result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			} else {
				if (isColor) {
					skinResources.getValue(skinId, value, true);
					result = (Drawable) ReflectionUtil.invoke(skinResources, "loadDrawable", loadParamType, value, id);
				} else {
					String file = value.string.toString();
					if (file.endsWith(".xml")) {
						try {
							XmlResourceParser rp = ReflectionUtil.invoke(this, "loadXmlResourceParser", new Class[] { String.class,
									int.class, int.class, String.class }, file, id, value.assetCookie, "drawable");
							result = Drawable.createFromXml(this, rp);
							rp.close();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}

				Object resultInfo = result instanceof ColorDrawable ? toHex(ReflectionUtil.getValue(result, "mState.mUseColor")) : result;
				Log.v(TAG, log + ",result:" + resultInfo + " from skinResources ");
				// 如果皮肤中存在要查找的资源, 但加载失败则直接从默认资源中加载
				if (result == null) {
					result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
				}
			}
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

		String log = "loadColorStateList " + toString(value);

		int key = (value.assetCookie << 24) | value.data;

		ColorStateList result = getCachedColorStateList(key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}
		boolean isColor = isColor(value);
		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
		} else if (isColor && value.resourceId < 0) {
			result = ReflectionUtil.invoke(skinResources, "loadColorStateList", loadParamType, value, id);
		} else {

			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(id);
			if (skinId == 0) {
				Log.v(TAG, log + " convertSkinId not found");
				result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			} else {
				if (isColor) {
					skinResources.getValue(skinId, value, true);
					result = ReflectionUtil.invoke(skinResources, "loadColorStateList", loadParamType, value, id);
				} else {
					String file = value.string.toString();
					if (file.endsWith(".xml")) {
						try {
							XmlResourceParser rp = ReflectionUtil.invoke(this, "loadXmlResourceParser", new Class[] { String.class,
									int.class, int.class, String.class }, file, id, value.assetCookie, "colorstatelist");
							result = ColorStateList.createFromXml(this, rp);
							rp.close();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
				Log.v(TAG, log + ",result:" + result + ", from resources :" + skinResources);
				// 如果皮肤中存在要查找的资源, 但加载失败则直接从默认资源中加载
				if (result == null) {
					result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
				}
			}
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
	
	

	public void clearCache() {
		skinIdMap.clear();
		notFoundedSkinIds.clear();
		resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];
		resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];
		drawableCache.clear();
		colorDrawableCache.clear();
		colorStateListCache.clear();
	}
}