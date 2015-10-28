package com.wy.test.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

public class ViewHolder implements AttrHolder {

	protected int background;
	
	@SuppressWarnings("deprecation")
	@Override
	public void reload(View view ,Resources res) {
		if (background != 0) {
			view.setBackgroundDrawable(res.getDrawable(background));
		}
	}

	@Override
	public void parse(View view ,AttributeSet attrs) {
		view.setTag(ATTR_HOLDER_KEY, this);
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
