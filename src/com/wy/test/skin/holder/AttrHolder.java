package com.wy.test.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

public interface AttrHolder {

	public static final int APP_RESOURCE_ID_PACKAGE = 0x7F000000;

	public static final int ATTR_HOLDER_KEY = 0x7FFFFFFF;

	/**
	 * 重新加载资源
	 * 
	 * @param view
	 */
	public void reload(View view, Resources res);

	public void parse(View view, AttributeSet attrs);
}
