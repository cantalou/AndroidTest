package com.wy.test.skin.resources;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class NightResources extends ProxyResources {

	/**
	 * 夜间模式资源名称前缀
	 */
	public static final String NIGHT_RESOURCE_NAME_SUF = "_night";

	/**
	 * 夜间模式资源id映射
	 */
	protected SparseIntArray nightIdMap = new SparseIntArray();

	/**
	 * 夜间模式资源不存在的id
	 */
	protected SparseIntArray notFoundedNightIds = new SparseIntArray();

	public NightResources(String packageName, Resources skinRes, Resources defRes, String skinName) {
		super(packageName, skinRes, defRes, skinName);
	}

	/**
	 * 将应用资源id转成夜间模式资源id
	 *
	 * @param id
	 * @return 夜间模式资源id
	 */
	public synchronized int toNightId(int id) {

		if (id == 0) {
			return 0;
		}

		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			return id;
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
		int index = name.lastIndexOf('.');
		if (index != -1) {
			name = name.substring(0, index) + NIGHT_RESOURCE_NAME_SUF + name.substring(index);
		} else {
			name = name + NIGHT_RESOURCE_NAME_SUF;
		}
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
		super.getValue(toNightId(id), outValue, resolveRefs);
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		super.getValueForDensity(toNightId(id), density, outValue, resolveRefs);
	}

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {
		Drawable result = super.loadDrawable(value, toNightId(id));
		if (result instanceof BitmapDrawable) {
			result.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
		}
		return result;
	}

	public void clearCache() {
		super.clearCache();
		nightIdMap.clear();
		notFoundedNightIds.clear();
	}

}
