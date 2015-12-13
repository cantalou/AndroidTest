package com.cantalou.skin.sparearray;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.TypedValue;

import com.cantalou.skin.res.SkinProxyResources;

public class ColorStateListSpareArray extends SparseArray<ColorStateList> {

	private LongSparseArray<Integer> resourceIdKeyMap;;

	/**
	 * Resources mColorStateListCache
	 */
	private SparseArray<ColorStateList> originalCache;

	private Resources resources;

	public ColorStateListSpareArray(Resources resources, SparseArray<ColorStateList> originalCache,
			LongSparseArray<Integer> resourceIdKeyMap) {
		this.resources = resources;
		this.originalCache = originalCache;
		this.resourceIdKeyMap = resourceIdKeyMap;
	}

	@Override
	public ColorStateList get(int key) {
		ColorStateList csl;
		Integer id = resourceIdKeyMap.get(key);
		if (id != null) {
			csl = resources.getColorStateList(id);
		} else {
			csl = originalCache.get(key);
		}
		return csl;
	}
}
