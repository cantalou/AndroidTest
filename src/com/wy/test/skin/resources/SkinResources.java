package com.wy.test.skin.resources;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

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
	 * @param res
	 */
	public SkinResources(AssetManager assets, Resources res) {
		super(assets, res.getDisplayMetrics(), res.getConfiguration());
	}
}
