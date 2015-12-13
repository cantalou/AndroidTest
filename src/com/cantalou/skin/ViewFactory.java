package com.cantalou.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.holder.AbstractHolder;
import com.cantalou.skin.holder.ImageViewHolder;
import com.cantalou.skin.holder.ListViewHolder;
import com.cantalou.skin.holder.TextViewHolder;
import com.cantalou.skin.holder.ViewHolder;

import java.util.HashMap;

/**
 * 自定义Factory的实现, 保存View中属性的资源信息, 如:background赋值的资源id
 *
 * @author LinZhiWei
 * @date 2015年11月29日 下午10:22:41
 */
public class ViewFactory implements Factory {

	private final String[] sClassPrefixList = { "android.widget.", "android.webkit.", "android.app." };

	private final HashMap<String, String> superNameCache = new HashMap<String, String>();

	private final HashMap<String, AbstractHolder> viewAttrHolder = new HashMap<String, AbstractHolder>();

	public ViewFactory() {
		viewAttrHolder.put("android.view.View", new ViewHolder());// for super class
		viewAttrHolder.put("View", new ViewHolder());// for layout file
		viewAttrHolder.put("android.widget.TextView", new TextViewHolder());
		viewAttrHolder.put("android.widget.ImageView", new ImageViewHolder());
		viewAttrHolder.put("android.widget.ListView", new ListViewHolder());
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = null;
		AbstractHolder attrHolder = getHolder(name);
		if (attrHolder != null) {
			attrHolder.parse(attrs);
		}
		try {
			LayoutInflater inflater = LayoutInflater.from(context);
			if (-1 == name.indexOf('.')) {
				for (String prefix : sClassPrefixList) {
					try {
						view = inflater.createView(name, prefix, attrs);
					} catch (ClassNotFoundException e) {
					}
				}
			} else {
				view = inflater.createView(name, null, attrs);
			}

			if (view == null) {
				return null;
			}

			if (attrHolder != null) {
				view.setTag(AbstractHolder.ATTR_HOLDER_KEY, attrHolder);
			}

			return view;

		} catch (InflateException e) {
			throw e;
		} catch (Exception e) {
			InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name + ", cause " + e);
			ie.initCause(e);
			throw ie;
		}
	}

	private AbstractHolder getHolder(String name) {

		AbstractHolder attrHolder = viewAttrHolder.get(name);
		if (attrHolder != null) {
			return attrHolder.clone();
		}

		if (-1 == name.indexOf('.')) {
			for (String prefix : sClassPrefixList) {
				try {
					return getHolder(getSuperClassName(prefix + name));
				} catch (ClassNotFoundException e) {
				}
			}
		} else {
			try {
				return getHolder(getSuperClassName(name));
			} catch (ClassNotFoundException e) {
			}
		}
		Log.w("can not find a AttrHolder associated with name :{}", name);
		return null;
	}

	private String getSuperClassName(String name) throws ClassNotFoundException {
		String superName = superNameCache.get(name);
		if (StringUtils.isNotBlank(superName)) {
			return superName;
		}

		Class<?> clazz = Class.forName(name);
		if (clazz != null && clazz.getSuperclass() != null) {
			superName = clazz.getSuperclass().getName();
			superNameCache.put(name, superName);
		}

		return superName;
	}

	public void registerAttrHolder(String name, AbstractHolder attrHolder) {
		viewAttrHolder.put(name, attrHolder);
	}
}
