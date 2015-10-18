package com.wy.test.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.TypedValue;

import com.wy.test.util.ReflectionUtil;

import java.lang.ref.WeakReference;

@SuppressWarnings("unchecked")
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
		defaultResources = new ProxyResources(cxt, skinRes, defRes, nightMode);
	}

	static Class<?>[] loadParamType = new Class<?>[] { TypedValue.class, int.class };

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {

		if (id == 0) {
			return null;
		}

		String log = "loadDrawable " + toString(value);

		long key = (((long) value.assetCookie) << 32) | value.data;
		boolean isColor = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
		LongSparseArray<WeakReference<ConstantState>> cache = isColor ? colorDrawableCache : drawableCache;

		Drawable result = getCachedDrawable(cache, key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}

		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			return result;
		} else {
			// 将app资源id转换成夜间模式资源id
			int nightId;
			if (nightMode && isColor) {
				nightId = toNightIdIfExist(id);
			} else {
				nightId = id;
			}

			Resources res = skinResources;

			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(nightId);
			if (skinId == 0 && nightId != id) {
				skinId = toSkinId(id);
			}
			if (skinId == 0) {
				if (nightId != id) {
					res.getValue(nightId, value, true);
				}
				Log.v(TAG, log + " convertId not found ");
				result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			} else {
				if (isColor) {
					res.getValue(skinId, value, true);
					result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
				} else {
					String file = value.string.toString();
					if (file.endsWith(".xml")) {
						try {
							XmlResourceParser rp = ReflectionUtil.invoke(res, "loadXmlResourceParser", new Class[] { String.class,
									int.class, int.class, String.class }, file, id, value.assetCookie, "drawable");
							result = Drawable.createFromXml(res, rp);
							rp.close();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					} else {
						result = (Drawable) ReflectionUtil.invoke(res, "loadDrawable", loadParamType, value, id);
					}
				}
				Object resultInfo = result instanceof ColorDrawable ? toHex((Integer) ReflectionUtil.getValue(
						ReflectionUtil.getValue(result, "mState"), "mUseColor")) : result;
				Log.v(TAG, log + ",result:" + resultInfo + " from skinResources ");
			}

			// 如果皮肤中存在要查找的资源, 但加载失败则直接从默认资源中加载
			if (result == null && skinId != 0) {
				result = (Drawable) ReflectionUtil.invoke(defaultResources, "loadDrawable", loadParamType, value, id);
			}

			if (nightMode && !isColor && result instanceof BitmapDrawable) {
				result.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
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

		String log = "loadColorStateList nightMode :" + nightMode + toString(value);

		int key = (value.assetCookie << 24) | value.data;

		ColorStateList result = getCachedColorStateList(key);
		if (result != null) {
			Log.v(TAG, log + " from cache ");
			return result;
		}

		// 系统资源
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			return result;
		} else {
			boolean isColor = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
			int nightId;
			if (nightMode && isColor) {
				nightId = toNightIdIfExist(id);
			} else {
				nightId = id;
			}

			// 将app资源id转换成皮肤资源id
			int skinId = toSkinId(nightId);
			if (skinId == 0 && nightId != id) {
				skinId = toSkinId(id);
			}
			if (skinId == 0) {
				if (nightId != id) {
					defaultResources.getValue(nightId, value, true);
				}
				Log.v(TAG, log + " convertSkinId not found");
				result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
			} else {
				Resources res = skinResources;
				if (isColor) {
					defaultResources.getValue(skinId, value, true);
					result = ReflectionUtil.invoke(res, "loadColorStateList", loadParamType, value, id);
				} else {
					String file = value.string.toString();
					try {
						XmlResourceParser rp = ReflectionUtil.invoke(res, "loadXmlResourceParser", new Class[] { String.class, int.class,
								int.class, String.class }, file, id, value.assetCookie, "colorstatelist");
						result = ColorStateList.createFromXml(this, rp);
						rp.close();
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
				}
				Log.v(TAG, log + ",result:" + result + ", from resources :" + res);
			}

			// 如果皮肤中存在要查找的资源, 但加载失败则直接从默认资源中加载
			if (result == null && skinId != 0) {
				result = ReflectionUtil.invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
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

	public synchronized void clearCache() {
		super.clearCache();
		drawableCache.clear();
		colorDrawableCache.clear();
		colorStateListCache.clear();
	}
}
