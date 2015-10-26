package com.wy.test.skin.holder;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class TextViewHolder extends ViewHolder {

	protected int textColorHighlight;
	protected int textColor;
	protected int textColorHint;
	protected int textColorLink;
	protected int drawableLeft;
	protected int drawableTop;
	protected int drawableRight;
	protected int drawableBottom;
	protected int shadowColor;
	protected int textCursorDrawable;

	@Override
	public void reload(View view) {
		super.reload(view);
	}

	@Override
	public void parse(AttributeSet attrs) {
		super.parse(attrs);
		textColorHighlight = getResourceId(attrs, "textColorHighlight");
		textColor = getResourceId(attrs, "textColor");
		textColorHint = getResourceId(attrs, "textColorHint");
		textColorLink = getResourceId(attrs, "textColorLink");
		drawableLeft = getResourceId(attrs, "drawableLeft");
		drawableTop = getResourceId(attrs, "drawableTop");
		drawableRight = getResourceId(attrs, "drawableRight");
		drawableBottom = getResourceId(attrs, "drawableBottom");
		shadowColor = getResourceId(attrs, "shadowColor");
		textCursorDrawable = getResourceId(attrs, "textCursorDrawable");
	}

}
