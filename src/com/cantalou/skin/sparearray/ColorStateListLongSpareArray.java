package com.cantalou.skin.sparearray;

import java.lang.reflect.TypeVariable;

import com.cantalou.skin.resources.ProxyResources;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.LongSparseArray;
import android.util.TypedValue;

/**
 *
 * @author LinZhiWei
 * @date 2015年12月9日 下午10:14:34
 */
public class ColorStateListLongSpareArray extends LongSparseArray<Drawable.ConstantState> {

	private LongSparseArray<Integer> resourceIdKeyMap = new LongSparseArray<Integer>();

	private ProxyResources resources;

	private TypedValue cacheValue = new TypedValue();

	public ColorStateListLongSpareArray(ProxyResources resources) {
		super();
		this.resources = resources;
	}

	/**
	 * 注册资源id, 添加和缓存key的映射
	 * 
	 * @param id
	 */
	public void registerResourceId(int id) {
		if ((ProxyResources.APP_ID_MASK & id) == ProxyResources.APP_ID_MASK) {
			synchronized (cacheValue) {
				TypedValue value = cacheValue;
				resources.getValue(id, value, true);
				resources.proxyLoadDrawable(value, id);
			}
		}
	}
}
