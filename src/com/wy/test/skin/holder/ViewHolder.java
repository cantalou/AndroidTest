package com.wy.test.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

public class ViewHolder implements AttrHolder {

	protected int background;

	@SuppressWarnings("deprecation")
	@Override
	public void reload(View view) {
		if (background != 0) {
			Resources res = view.getResources();
			view.setBackgroundDrawable(res.getDrawable(background));
		}
	}

	@Override
	public void parse(AttributeSet attrs) {
		background = getResourceId(attrs, "background");
	}

	protected int getResourceId(AttributeSet attrs, String name) {
		int len = attrs.getAttributeCount();
		for (int i = 0; i < len; i++) {
			if (name.equals(attrs.getAttributeName(i))) {
				return attrs.getAttributeNameResource(i);
			}
		}
		return 0;
	}

}
