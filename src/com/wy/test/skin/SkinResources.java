package com.wy.test.skin;

import java.io.IOException;
import java.io.InputStream;

import net.sf.cglib.core.ReflectUtils;

import org.xmlpull.v1.XmlPullParserException;

import com.wy.test.util.ReflectionUtil;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;

public class SkinResources extends Resources {

	public SkinResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
		super(assets, metrics, config);
	}

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param assets
	 *            Previously created AssetManager.
	 * @param cxt
	 * @param res
	 */
	public SkinResources(AssetManager assets, Resources res) {
		super(assets, res.getDisplayMetrics(), res.getConfiguration());
	}
}
