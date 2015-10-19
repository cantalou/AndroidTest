package com.wy.test.skin;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class ProxyResources extends Resources {

	private static final String TAG = "ProxyResources";

	/**
	 * app资源id前缀
	 */
	public static final int APP_ID_MASK = 0x7F000000;

	/**
	 * 资源名称缓存数量
	 */
	public static final int RESOURCE_NAME_CACHE_SIZE = 31;

	/**
	 * 夜间模式资源名称前缀
	 */
	public static final String NIGHT_RESOURCE_NAME_PRE = "night_";

	/**
	 * 皮肤资源id映射
	 */
	protected SparseIntArray skinIdMap = new SparseIntArray();

	/**
	 * 夜间模式资源id映射
	 */
	protected SparseIntArray nightIdMap = new SparseIntArray();

	/**
	 * 皮肤资源不存在的id
	 */
	protected SparseIntArray notFoundedSkinIds = new SparseIntArray();

	/**
	 * 夜间模式资源不存在的id
	 */
	protected SparseIntArray notFoundedNightIds = new SparseIntArray();

	/**
	 * 皮肤资源
	 */
	protected Resources skinResources;

	/**
	 * 默认资源
	 */
	private Resources defaultResources;

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
	 * 是否夜间模式
	 */
	protected boolean nightMode = false;

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
	public ProxyResources(Context cxt, Resources skinRes, Resources defRes, boolean nightMode) {
		super(defRes.getAssets(), defRes.getDisplayMetrics(), defRes.getConfiguration());
		skinResources = skinRes;
		defaultResources = defRes;
		packageName = cxt.getPackageName();
		this.nightMode = nightMode;
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
	public ProxyResources(Context cxt, Resources skinRes, Resources defRes) {
		this(cxt, skinRes, defRes, false);
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

	/**
	 * 将应用资源id转成夜间模式资源id
	 *
	 * @param id
	 * @return 夜间模式资源id
	 */
	public synchronized int toNightIdIfExist(int id) {

		if (id == 0) {
			return 0;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回0
		if (notFoundedNightIds.get(id) > 0) {
			return id;
		}

		int nightId = nightIdMap.get(id);
		if (nightId != 0) {
			return nightId;
		}

		String name = getResourceName(id);
		if (TextUtils.isEmpty(name)) {
			return id;
		}
		int index = name.indexOf('/') + 1;
		name = name.substring(0, index) + NIGHT_RESOURCE_NAME_PRE + name.substring(index);
		nightId = getIdentifier(name, null, packageName);
		if (nightId == 0) {
			notFoundedNightIds.put(id, id);
			nightId = id;
		} else {
			nightIdMap.put(id, nightId);
		}
		return nightId;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			super.getValue(id, outValue, resolveRefs);
			return;
		}

		if (nightMode) {
			id = toNightIdIfExist(id);
		}
		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValue(skinId, outValue, resolveRefs);
		} else {
			super.getValue(id, outValue, resolveRefs);
		}
		Log.v(TAG, "getValue nightMode :" + nightMode + toString(outValue));
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			super.getValueForDensity(id, density, outValue, resolveRefs);
			return;
		}

		if (nightMode) {
			id = toNightIdIfExist(id);
		}

		int skinId = toSkinId(id);
		if (skinId != 0) {
			skinResources.getValueForDensity(skinId, density, outValue, resolveRefs);
		} else {
			super.getValueForDensity(id, density, outValue, resolveRefs);
		}
		Log.v(TAG, "getValueForDensity nightMode :" + nightMode + toString(outValue));
	}

	protected String toString(TypedValue value) {
		return " skinId:" + toHex(toSkinId(value.resourceId))
				+ (TextUtils.isEmpty(value.string) ? value + ",name:" + getResourceName(value.resourceId) : value.toString());
	}

	protected String toHex(Object id) {
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
			resourceNameCache[index] = name;
			return name;
		} catch (Exception e) {
			return null;
		}
	}

	public void clearCache() {
		skinIdMap.clear();
		nightIdMap.clear();
		notFoundedSkinIds.clear();
		notFoundedNightIds.clear();
		resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];
		resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];
	}
}
