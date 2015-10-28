package com.wy.test.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ImageViewHolder extends ViewHolder {

	protected int src;

	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);
		((ImageView) view).setImageDrawable(res.getDrawable(src));
	}

	@Override
	public void parse(View view, AttributeSet attrs) {
		super.parse(view, attrs);
		src = getResourceId(attrs, "src");
	}
}
