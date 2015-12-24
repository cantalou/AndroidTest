package com.cantalou.skin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.PrefUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.holder.AbstractHolder;
import com.cantalou.skin.instrumentation.SkinInstrumentation;
import com.cantalou.skin.res.NightResources;
import com.cantalou.skin.res.ProxyResources;
import com.cantalou.skin.res.SkinProxyResources;
import com.cantalou.skin.res.SkinResources;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * 皮肤资源Manager
 *
 * @author LinZhiWei
 * @date 2015年10月31日 下午3:49:46
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SkinManager {
	/**
	 * 当前皮肤存储key
	 */
	public static final String PREF_KEY_CURRENT_SKIN = "com.cantalou.skin.PREF_KEY_CURRENT_SKIN";

	/**
	 * 默认皮肤
	 */
	public static final String DEFAULT_SKIN_PATH = "defaultSkin";

	/**
	 * 夜间模式皮肤资源名称, 夜间模式属于内置资源包
	 */
	public static final String DEFAULT_SKIN_NIGHT = "defaultSkinNight";

	/**
	 * activity
	 */
	ArrayList<Activity> activitys = new ArrayList<Activity>();

	/**
	 * 已载入的资源
	 */
	private HashMap<String, WeakReference<Resources>> cacheResources = new HashMap<String, WeakReference<Resources>>();

	/**
	 * 当前是否正在切换资源
	 */
	volatile boolean changingResource = false;

	/**
	 * 默认资源
	 */
	private ProxyResources defaultResources;

	/**
	 * 资源名称
	 */
	String currentSkinPath = DEFAULT_SKIN_PATH;

	/**
	 * 资源
	 */
	private Resources currentSkinResources;

	/**
	 * 资源切换时提交View刷新任务到UI线程
	 */
	private Handler handler = new Handler(Looper.myLooper());

	/**
	 * 资源切换结束回调
	 */
	private ArrayList<OnResourcesChangeFinishListener> onResourcesChangeFinishListeners = new ArrayList<OnResourcesChangeFinishListener>();

	/**
	 * 缓存对象
	 */
	private TypedValue cacheValue = new TypedValue();

	/**
	 * 资源id与key的映射
	 */
	private LongSparseArray<Integer> drawableIdKeyMap = new LongSparseArray<Integer>();

	/**
	 * 颜色资源id与key的映射
	 */
	private LongSparseArray<Integer> colorDrawableIdKeyMap = new LongSparseArray<Integer>();

	/**
	 * 文字颜色资源id与key的映射
	 */
	private LongSparseArray<Integer> colorStateListIdKeyMap = new LongSparseArray<Integer>();

	private static class InstanceHolder {
		static final com.cantalou.skin.SkinManager INSTANCE = new com.cantalou.skin.SkinManager();
	}

	private SkinManager() {
	}

	public static com.cantalou.skin.SkinManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * 通过替换ActivityThread中的mInstrumentation属性, 拦截Activity的生命周期回调, 添加皮肤功能
	 */
	public void initByReplaceInstrumentation(Context cxt) {

		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("applicationOnCreate method can only be called in the main thread");
		}

		Class<?> activityThreadClass = forName("android.app.ActivityThread");
		if (activityThreadClass == null) {
			Log.w("Fail to load class android.app.ActivityThread. Try invoking onAttach in Activity.onAttach method before invoking super.onAttach");
			return;
		}

		Object activityThread = invoke(activityThreadClass, "currentActivityThread");
		if (activityThread == null) {
			Log.w("Fail to get ActivityThread instance. Try invoking onAttach in Activity.onAttach method before invoking super.onAttach");
			return;
		}

		Instrumentation instrumentation = invoke(activityThreadClass, "getInstrumentation");
		if (instrumentation == null) {
			Log.w("Can not load class android.app.ActivityThread. Try invoking onAttach in Activity.onAttach method before invoking super.onAttach");
			return;
		}

		SkinInstrumentation skinInstrumentation = new SkinInstrumentation(this, instrumentation);
		if (!set(activityThread, "mInstrumentation", skinInstrumentation)) {
			Log.w("Fail to replace field named mInstrumentation . Try invoking onAttach in Activity.onAttach method before invoking super.onAttach");
		}

	}

	/**
	 * 创建皮肤资源
	 *
	 * @param skinPath
	 *            资源文件路径
	 * @return 资源对象
	 */
	private Resources createSkinResource(String skinPath) {

		if (DEFAULT_SKIN_NIGHT.equals(skinPath)) {
			return defaultResources;
		}

		Resources skinResources = null;

		File skinFile = new File(skinPath);
		if (!skinFile.exists()) {
			Log.w(skinFile + " does not exist");
			return null;
		}

		try {
			AssetManager am = AssetManager.class.newInstance();
			int result = invoke(am, "addAssetPath", new Class<?>[] { String.class }, skinFile.getAbsolutePath());
			if (result == 0) {
				Log.w("AssetManager.addAssetPath return 0. Initial AssetManager fail. ");
				return null;
			} else {
				skinResources = new SkinResources(am, defaultResources, skinPath);
			}
		} catch (Exception e) {
			Log.e(e, "初始化AssetManager失败");
		}
		return skinResources;
	}

	/**
	 * 创建代理资源
	 *
	 * @param cxt
	 * @param skinPath
	 *            资源路径
	 * @return 代理Resources, 如果skinPath文件不存在或者解析失败返回null
	 */
	private Resources createProxyResource(Context cxt, String skinPath) {

		if (DEFAULT_SKIN_PATH.equals(skinPath)) {
			Log.d("skinPath is:{} , return defaultResources");
			return defaultResources;
		}

		Resources skinResources = null;
		WeakReference<Resources> resRef = cacheResources.get(skinPath);
		if (resRef != null) {
			skinResources = resRef.get();
			if (skinResources != null) {
				return skinResources;
			}
		}

		skinResources = createSkinResource(skinPath);
		if (skinResources == null) {
			Log.w("Create skin resources fail");
			return skinResources;
		}

		if (DEFAULT_SKIN_NIGHT.equals(skinPath)) {
			skinResources = new NightResources(cxt.getPackageName(), skinResources, defaultResources, skinPath);
		} else {
			skinResources = new SkinProxyResources(cxt.getPackageName(), skinResources, defaultResources, skinPath);
		}

		synchronized (this) {
			cacheResources.put(skinPath, new WeakReference<Resources>(skinResources));
		}
		return skinResources;
	}

	/**
	 * 将Activity或者Context的资源替换成toRes指定资源
	 *
	 * @param activity
	 *            触发切换资源的Activity
	 * @param toRes
	 *            新资源
	 */
	private void changeActivityResources(Activity activity, Resources toRes) {
		// ContextThemeWrapper add mResources field in JELLY_BEAN
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Log.v("after JELLY_BEAN change Activity:{} to Resources :{} ,result:{} ", activity, toRes, set(activity, "mResources", toRes));
		} else {
			Log.v("vefore JELLY_BEAN change context:{} to Resources :{} ,result:{} ", activity.getBaseContext(), toRes,
					set(activity.getBaseContext(), "mResources", toRes));
		}
		Log.v("reset theme to null ", set(activity, "mTheme", null));
	}

	/**
	 * 注册自定义的ViewFactory
	 *
	 * @param cxt
	 */
	private void registerViewFactory(Activity activity) {
		LayoutInflater li = activity.getLayoutInflater();
		Factory factory = li.getFactory();
		if (factory == null) {
			li.setFactory(new ViewFactory(li));
			Log.d("LayoutInflater register custom factory");
		} else {
			li.setFactory(new ViewFactory(li, factory));
			Log.d("LayoutInflater register custom proxy factory");
		}
	}

	/**
	 * 更换所有activity的皮肤资源
	 *
	 * @param activity
	 * @param skinPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void changeResources(Activity activity, final String skinPath) {
		if (StringUtils.isBlank(skinPath)) {
			throw new IllegalArgumentException("skinPath could not be empty");
		}

		if (defaultResources == null) {
			throw new IllegalStateException("defaultResources is not initialized. Call the method onAttach of SkinManage in Activity onAttach()");
		}

		final Context cxt = activity.getApplicationContext();
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				changingResource = true;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Log.d("start change resource");
					Resources res = createProxyResource(cxt, skinPath);
					if (res == null) {
						return false;
					}
					((ProxyResources) res).replacePreloadCache();
					List<Activity> temp = (List<Activity>) activitys.clone();
					for (int i = temp.size() - 1; i >= 0; i--) {
						Log.d("change :{} resources to :{}", temp.get(i), res);
						change(temp.get(i), res);
					}
					Log.d("finish change resource");
					currentSkinResources = res;
					currentSkinPath = skinPath;
					PrefUtil.setString(cxt, PREF_KEY_CURRENT_SKIN, currentSkinPath);
					return true;
				} catch (Exception e) {
					Log.e(e);
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				Log.i("changeResources doInBackground return :{}, currentSkin:{}", result, currentSkinPath);
				ArrayList<OnResourcesChangeFinishListener> list = (ArrayList<OnResourcesChangeFinishListener>) onResourcesChangeFinishListeners
						.clone();
				for (OnResourcesChangeFinishListener listener : list) {
					listener.onResourcesChangeFinish(result);
				}
				changingResource = false;
			}
		}.execute();

		showSkinChangeAnimation(activity);
	}

	/**
	 * 更换所有activity的皮肤资源, 调用OnResourcesChangeListener回调进行自定义资源的更新
	 *
	 * @param a
	 *            activity
	 * @param res
	 *            资源
	 */
	void change(final Activity a, Resources res) {

		changeActivityResources(a, res);

		if (a instanceof Skinable) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					((Skinable) a).onResourcesChange();
				}
			});
		}

		final List<?> fragments = get(a, "mFragments.mAdded");
		if (fragments != null && fragments.size() > 0) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					for (Object f : fragments) {
						if (f instanceof Skinable) {
							final Skinable listener = (Skinable) f;
							listener.onResourcesChange();
						}
					}
				}
			});
		}

		final Window w = a.getWindow();
		if (w != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					onResourcesChange(w.getDecorView());
				}
			});
		}
	}

	/**
	 * 递归调用实现了OnResourcesChangeListener接口的View
	 *
	 * @param v
	 */
	private void onResourcesChange(final View v) {

		if (v == null) {
			return;
		}

		if (v instanceof Skinable) {
			((Skinable) v).onResourcesChange();
			v.invalidate();
		}

		Object tag = v.getTag(AbstractHolder.ATTR_HOLDER_KEY);
		if (tag != null && tag instanceof AbstractHolder) {
			((AbstractHolder) tag).reload(v, v.getContext().getResources());
		}

		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			int size = vg.getChildCount();
			for (int i = 0; i < size; i++) {
				onResourcesChange(vg.getChildAt(i));
			}
		}
	}

	/**
	 * 初始化defaultResources, 判断使用什么资源,
	 *
	 * @param activity
	 */
	public void onAttach(Activity activity) {

		if (defaultResources == null) {
			defaultResources = new ProxyResources(activity.getResources());
			defaultResources.replacePreloadCache();
			Log.v("init defaultResources and registerViewFactory ");
		}

		registerViewFactory(activity);

		activitys.add(activity);

		String prefSkinPath = PrefUtil.getString(activity, PREF_KEY_CURRENT_SKIN);
		if (StringUtils.isNotBlank(prefSkinPath)) {
			currentSkinPath = prefSkinPath;
		}

		Resources res;
		if (DEFAULT_SKIN_PATH.equals(currentSkinPath)) {
			res = defaultResources;
		} else {
			res = createProxyResource(activity, currentSkinPath);
		}

		try {
			changeActivityResources(activity, res);
		} catch (Exception e) {
			Log.e(e);
		}
	}

	/**
	 * 对当前界面截图, 模糊渐变消失
	 *
	 * @param activity
	 *            要显示渐变动画的界面
	 */
	private void showSkinChangeAnimation(Activity activity) {
		try {
			final ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
			if (decor == null) {
				return;
			}

			decor.setDrawingCacheEnabled(true);
			Bitmap temp = Bitmap.createBitmap(decor.getDrawingCache());
			decor.setDrawingCacheEnabled(false);
			final ImageView iv = new ImageView(activity);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// consume all event
				}
			});
			iv.setImageBitmap(temp);

			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			decor.addView(iv, lp);

			final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
			alphaAnimation.setDuration(800);
			iv.setAnimation(alphaAnimation);
			alphaAnimation.startNow();
			decor.postDelayed(new Runnable() {
				@Override
				public void run() {
					alphaAnimation.reset();
					alphaAnimation.cancel();
					iv.setAnimation(null);
					iv.setVisibility(View.GONE);
					decor.removeView(iv);
				}
			}, 800);

		} catch (Exception e) {
			Log.e(e);
		}
	}

	public void onDestroy(Activity activity) {
		activitys.remove(activity);
	}

	/**
	 * 注册图片资源id
	 */
	public synchronized void registerDrawable(int id) {

		if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
			return;
		}

		TypedValue value = cacheValue;
		defaultResources.getValue(id, value, true);
		long key = 0;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
			key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
			if (isColorDrawable) {
				colorDrawableIdKeyMap.put(key, id);
			} else {
				drawableIdKeyMap.put(key, id);
			}
		} else {
			key = (((long) value.assetCookie) << 32) | value.data;
			drawableIdKeyMap.put(key, id);
		}

	}

	/**
	 * 注册资源id
	 */
	public synchronized void registerColorStateList(int id) {

		if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
			return;
		}

		TypedValue value = cacheValue;
		defaultResources.getValue(id, value, true);
		long key;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			key = (((long) value.assetCookie) << 32) | value.data;
		} else {
			key = (value.assetCookie << 24) | value.data;
		}
		colorStateListIdKeyMap.put(key, id);
	}

	/**
	 * 当前是否正在切换资源
	 *
	 * @return 是 true
	 */
	public boolean isChangingResource() {
		return changingResource;
	}

	public String getCurrentSkin() {
		return currentSkinPath;
	}

	public synchronized void addOnResourcesChangeFinishListener(OnResourcesChangeFinishListener listener) {
		onResourcesChangeFinishListeners.add(listener);
	}

	public synchronized void removeOnResourcesChangeFinishListener(OnResourcesChangeFinishListener listener) {
		onResourcesChangeFinishListeners.remove(listener);
	}

	public Resources getCurrentSkinResources() {
		return currentSkinResources;
	}

	public Resources getDefaultResources() {
		return defaultResources;
	}

	public LongSparseArray<Integer> getDrawableIdKeyMap() {
		return drawableIdKeyMap;
	}

	public LongSparseArray<Integer> getColorStateListIdKeyMap() {
		return colorStateListIdKeyMap;
	}

	public LongSparseArray<Integer> getColorDrawableIdKeyMap() {
		return colorDrawableIdKeyMap;
	}

}
