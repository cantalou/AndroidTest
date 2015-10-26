package com.wy.test.skin;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

public class SkinInflaterFactory implements Factory {

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = createView(context, name, attrs);

		if (view == null) {
			return null;
		}

		parseSkinAttr(context, attrs, view);

		return view;
	}

	private View createView(Context context, String name, AttributeSet attrs) {
		View view = null;
		try {
			if (-1 == name.indexOf('.')) {
				if ("View".equals(name)) {
					view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
				}
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
				}
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
				}
			} else {
				view = LayoutInflater.from(context).createView(name, null, attrs);
			}
		} catch (Exception e) {
			view = null;
		}
		return view;
	}

	/**
	 * Collect skin able tag such as background , textColor and so on
	 * 
	 * @param context
	 * @param attrs
	 * @param view
	 */
	private void parseSkinAttr(Context context, AttributeSet attrs, View view) {

		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			String attrName = attrs.getAttributeName(i);
			String attrValue = attrs.getAttributeValue(i);
			attrs.get

			if (attrValue.startsWith("@")) {
				try {
					int id = Integer.parseInt(attrValue.substring(1));
					String entryName = context.getResources().getResourceEntryName(id);
					String typeName = context.getResources().getResourceTypeName(id);
					SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
					if (mSkinAttr != null) {
						viewAttrs.add(mSkinAttr);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		if (!ListUtils.isEmpty(viewAttrs)) {
			SkinItem skinItem = new SkinItem();
			skinItem.view = view;
			skinItem.attrs = viewAttrs;

			mSkinItems.add(skinItem);

			if (SkinManager.getInstance().isExternalSkin()) {
				skinItem.apply();
			}
		}
	}

	public void applySkin() {
		if (ListUtils.isEmpty(mSkinItems)) {
			return;
		}

		for (SkinItem si : mSkinItems) {
			if (si.view == null) {
				continue;
			}
			si.apply();
		}
	}

	public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> pDAttrs) {
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;

		for (DynamicAttr dAttr : pDAttrs) {
			int id = dAttr.refResId;
			String entryName = context.getResources().getResourceEntryName(id);
			String typeName = context.getResources().getResourceTypeName(id);
			SkinAttr mSkinAttr = AttrFactory.get(dAttr.attrName, id, entryName, typeName);
			viewAttrs.add(mSkinAttr);
		}

		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}

	public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId) {
		int id = attrValueResId;
		String entryName = context.getResources().getResourceEntryName(id);
		String typeName = context.getResources().getResourceTypeName(id);
		SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		viewAttrs.add(mSkinAttr);
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}

	public void addSkinView(SkinItem item) {
		mSkinItems.add(item);
	}

	public void clean() {
		if (ListUtils.isEmpty(mSkinItems)) {
			return;
		}

		for (SkinItem si : mSkinItems) {
			if (si.view == null) {
				continue;
			}
			si.clean();
		}
	}
}
