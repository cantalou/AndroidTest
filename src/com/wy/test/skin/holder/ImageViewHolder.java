package com.wy.test.skin.holder;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ImageViewHolder extends ViewHolder {

	protected int src;

	@Override
	public void reload(View view) {
		super.reload(view);
	}

	@Override
	public void parse(AttributeSet attrs) {
		super.parse(attrs);
		src = getResourceId(attrs, "src");
	}
}
