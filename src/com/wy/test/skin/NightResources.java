package com.wy.test.skin;

import java.lang.ref.WeakReference;

import com.wy.test.util.ReflectionUtil;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class NightResources extends ProxyResources {

	/**
	 * 夜间模式资源名称前缀
	 */
	public static final String NIGHT_RESOURCE_NAME_PRE = "night_";

	/**
	 * 夜间模式资源id映射
	 */
	protected SparseIntArray nightIdMap = new SparseIntArray();

	/**
	 * 夜间模式资源不存在的id
	 */
	protected SparseIntArray notFoundedNightIds = new SparseIntArray();

	public NightResources(Context cxt, Resources skinRes, Resources defRes) {
		super(cxt, skinRes, defRes);
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

		int nightId = toNightId(id);
		if (nightId != 0) {
			defaultResources.getValue(nightId, outValue, resolveRefs);
			outValue.resourceId = -outValue.resourceId;
		} else {
			defaultResources.getValue(id, outValue, resolveRefs);
		}
		Log.v(TAG, "getValue " + toString(outValue));
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			super.getValueForDensity(id, density, outValue, resolveRefs);
			return;
		}

		int nightId = toNightId(id);
		if (nightId != 0) {
			defaultResources.getValueForDensity(nightId, density, outValue, resolveRefs);
			outValue.resourceId = -outValue.resourceId;
		} else {
			defaultResources.getValueForDensity(id, density, outValue, resolveRefs);
		}
		Log.v(TAG, "getValueForDensity " + toString(outValue));
	}

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {
		
		if (value.resourceId > 0 && isColor(value)) {
			getValue(id, value, true);
		}
		
		Drawable result = super.loadDrawable(value, id);
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
