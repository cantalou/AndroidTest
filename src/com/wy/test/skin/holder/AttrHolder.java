package com.wy.test.skin.holder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public interface AttrHolder {

	/**
	 * 重新加载资源
	 * 
	 * @param view
	 */
	public void reload(View view);

	public void parse(AttributeSet attrs);
}
