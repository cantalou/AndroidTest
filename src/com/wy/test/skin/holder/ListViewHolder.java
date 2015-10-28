package com.wy.test.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListViewHolder extends ViewHolder {

	protected int divider;

	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);
		((ListView) view).setDivider(res.getDrawable(divider));
	}

	@Override
	public void parse(View view, AttributeSet attrs) {
		super.parse(view, attrs);
		divider = getResourceId(attrs, "divider");
	}
}
