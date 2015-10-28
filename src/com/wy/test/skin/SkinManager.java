package com.wy.test.skin;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wy.test.skin.resources.NightResources;
import com.wy.test.skin.resources.ProxyResources;
import com.wy.test.skin.resources.SkinResources;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.wy.test.util.FileUtil.copyAssetsFile;
import static com.wy.test.util.PrefUtil.get;
import static com.wy.test.util.PrefUtil.getBoolean;
import static com.wy.test.util.ReflectionUtil.invoke;
import static com.wy.test.util.ReflectionUtil.setValue;

public class SkinManager
{

    private static final String TAG = "SkinManager";

    private static final String SKIN_FILE_NAME = "skin.apk";

    /**
     * 默认资源
     */
    private Resources defaultResources;

    /**
     * 代理资源
     */
    private Resources proxyResources;

    /**
     * 代理资源
     */
    private Resources nightProxyResources;

    private static class InstanceHolder
    {
        static final SkinManager INSTANCE = new SkinManager();
    }

    private SkinManager()
    {
    }

    public static SkinManager getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context cxt){
        registerViewFactory(cxt);
    }

    private Resources createSkinResource(Activity activity, String path) throws Exception
    {
        Resources skinResources = null;
        if (!TextUtils.isEmpty(path))
        {
            String skinDir = activity.getFilesDir()
                                     .getAbsolutePath();
            File f = new File(skinDir, SKIN_FILE_NAME);
            if (!f.exists())
            {
                copyAssetsFile(activity, SKIN_FILE_NAME, skinDir, SKIN_FILE_NAME);
            }

            AssetManager am = AssetManager.class.newInstance();
            invoke(am, "addAssetPath", new Class<?>[]{String.class}, f.getAbsolutePath());
            skinResources = new SkinResources(am, defaultResources);
        }
        return skinResources;
    }

    private Resources createProxyResource(Activity activity, String path, boolean night) throws Exception
    {
        if (defaultResources == null)
        {
            defaultResources = activity.getResources();
        }
        if (night)
        {
            nightProxyResources = new NightResources(activity, createSkinResource(activity, path), defaultResources);
            return nightProxyResources;
        }
        else
        {
            proxyResources = new ProxyResources(activity, createSkinResource(activity, path), defaultResources);
            return proxyResources;
        }
    }

    public void toggle(Activity activity)
    {
        changeResources(activity);
    }

    public void changeResources(Activity activity)
    {
        try
        {
            String skinPath = get(activity, "skinPath");
            boolean night = getBoolean(activity, "night");
            Log.d(TAG, "skinPath:" + skinPath + ",night:" + night);

            if (defaultResources == null)
            {
                defaultResources = activity.getResources();
            }

            Resources res = null;
            if (TextUtils.isEmpty(skinPath) && !night)
            {
                res = defaultResources;
            }
            else
            {
                res = createProxyResource(activity, skinPath, night);
            }

            if (activity.getResources() == res)
            {
                return;
            }

            // ContextThemeWrapper add mResources field in JELLY_BEAN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                setValue(activity, "mResources", res);
            }
            else
            {
                setValue(activity.getBaseContext(), "mResources", res);
            }
            setValue(activity, "mTheme", null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 注册自定义的ViewFactory
     *
     * @param cxt
     */
    public void registerViewFactory(Context cxt)
    {
        LayoutInflater li = LayoutInflater.from(cxt);
        if (li.getFactory() == null)
        {
            li.setFactory(new ViewFactory());
        }
    }
}

package com.m4399.forums.manager.skin;

        import android.app.Activity;
        import android.content.Context;
        import android.content.res.AssetManager;
        import android.content.res.Configuration;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.os.Build;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentActivity;
        import android.util.DisplayMetrics;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.AbsListView;
        import android.widget.ImageView;

        import com.m4399.forums.base.constance.ForumsGlobal;
        import com.m4399.forums.manager.config.AppConfigKey;
        import com.m4399.forums.manager.config.AppConfigManager;
        import com.m4399.forumslib.utils.ActivityStateUtil;
        import com.m4399.forumslib.utils.MyLog;
        import com.m4399.forumslib.utils.ReflectionUtil;
        import com.m4399.forumslib.utils.StringUtils;
        import com.nineoldandroids.animation.Animator;
        import com.nineoldandroids.animation.AnimatorListenerAdapter;
        import com.nineoldandroids.view.ViewPropertyAnimator;

        import java.io.File;
        import java.util.HashMap;
        import java.util.List;

        import static com.m4399.forumslib.utils.ReflectionUtil.invoke;
        import static com.m4399.forumslib.utils.ReflectionUtil.setValue;

/**
 * Project Name: m4399_Forums
 * File Name:    SkinManager.java
 * ClassName:    SkinManager
 *
 * Description: 皮肤管理器.
 *
 * @author LinZhiWei
 * @date 2015年07月25日 15:27
 *
 * Copyright (c) 2015年, 4399 Network CO.ltd. All Rights Reserved.
 */
public class SkinManager
{
    private static final String TAG = "SkinManager";

    /**
     * 默认皮肤,apk自带的资源
     */
    public static final String DEFAULT_SKIN_NAME = "defaultSkinName";

    /**
     * activity与资源map,用于保存activity改变皮肤前的resources
     */
    private HashMap<Activity, Resources> activityMaps = new HashMap<Activity, Resources>();

    /**
     * 用户皮肤资源资源
     */
    private Resources skinResources;

    /**
     * 当前是否正在显示切换皮肤动画
     */
    volatile boolean isAnimationing = false;


    private static class InstanceHolder
    {
        static final SkinManager INSTANCE = new SkinManager();
    }

    private SkinManager()
    {
    }

    public static SkinManager getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 初始化皮肤
     */
    public synchronized void init(Context cxt)
    {
    }

    /**
     * 加载皮肤资源
     *
     * @param name 皮肤资源名称
     */
    public synchronized boolean loadSkin(Activity activity, String name)
    {
        if (StringUtils.isBlank(name))
        {
            return true;
        }

        if (name.equals(AppConfigManager.getValue(AppConfigKey.SETTING_SKIN_FILE_NAME)))
        {
            return true;
        }

        if (!activityMaps.containsKey(activity))
        {
            activityMaps.put(activity, activity.getResources());
        }

        if (DEFAULT_SKIN_NAME.equals(name))
        {
            AppConfigManager.setValue(AppConfigKey.SETTING_SKIN_FILE_NAME, name);
            skinResources = activityMaps.get(activity);
            changeAll(activity);
            return true;
        }
        else
        {
            createResources(activity, name);
            if (skinResources != null)
            {
                AppConfigManager.setValue(AppConfigKey.SETTING_SKIN_FILE_NAME, name);
                changeAll(activity);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * 更换activity的皮肤资源
     *
     * @param activity 要更新皮肤的界面
     */
    boolean changeResources(Activity activity)
    {
        if (skinResources != null && activity.getResources() != skinResources)
        {
            setValue(activity, "mResources", skinResources);
            setValue(activity, "mTheme", null);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            {
                setValue(activity.getBaseContext(), "mResources", skinResources);
            }
            return true;
        }
        return false;
    }

    /**
     * 更换所有activity的皮肤资源
     */
    private void changeAll(Activity activity)
    {
        if (ActivityStateUtil.isDestroy(activity))
        {
            return;
        }

        //在改变皮肤设置前,截图用于渐变动画显示
        skinChangeAnimation(activity);

        //遍历所有的activity调用onSkinChange修改资源
        for (Activity a : activityMaps.keySet())
        {
            MyLog.d(TAG, "change resources {}", a);

            changeResources(a);

            if (a instanceof OnResourcesChangeListener)
            {
                ((OnResourcesChangeListener) a).onResourcesChange();
            }

            if (a instanceof FragmentActivity)
            {
                List<Fragment> fragments = ((FragmentActivity) a).getSupportFragmentManager()
                                                                 .getFragments();
                if (fragments == null || fragments.size() == 0)
                {
                    continue;
                }
                for (Fragment f : fragments)
                {
                    if (f instanceof OnResourcesChangeListener)
                    {
                        ((OnResourcesChangeListener) f).onResourcesChange();
                    }
                }
            }

            Window w = a.getWindow();
            if (w != null)
            {
                onResourcesChange(w.getDecorView());
            }
        }
    }

    private void onResourcesChange(View v)
    {
        if (v == null)
        {
            return;
        }
        if (v instanceof OnResourcesChangeListener)
        {
            ((OnResourcesChangeListener) v).onResourcesChange();
            v.invalidate();
        }

        if (v instanceof AbsListView)
        {
            Object recycler = ReflectionUtil.getValue(v, "mRecycler");
            ReflectionUtil.invoke(recycler, "scrapActiveViews", new Class[0], new Object[0]);
            ReflectionUtil.invoke(recycler, "clear", new Class[0], new Object[0]);
        }

        if (v instanceof ViewGroup)
        {
            ViewGroup vg = (ViewGroup) v;
            int size = vg.getChildCount();
            for (int i = 0; i < size; i++)
            {
                onResourcesChange(vg.getChildAt(i));
            }
        }
    }

    /**
     * 更换皮肤
     *
     * @param activity 要更新皮肤的界面
     */
    public synchronized void changeSkinOnAttach(Activity activity)
    {
        if (!activityMaps.containsKey(activity))
        {
            activityMaps.put(activity, activity.getResources());
        }

        String skinName = AppConfigManager.getValue(AppConfigKey.SETTING_SKIN_FILE_NAME);
        if (StringUtils.isBlank(skinName) || DEFAULT_SKIN_NAME.equals(skinName))
        {
            return;
        }

        if (skinResources == null)
        {
            createResources(activity, skinName);
        }
        if (skinResources != null && activity.getResources() != skinResources)
        {
            changeResources(activity);
        }
    }

    private void createResources(Activity cxt, String skinName)
    {
        if (StringUtils.isBlank(skinName))
        {
            return;
        }

        String filePath = ForumsGlobal.DIR_SKIN + File.separator + skinName;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                MyLog.w(TAG, "文件不存在,地址:{}", filePath);
                return;
            }

            if (!file.canRead())
            {
                MyLog.w(TAG, "文件不可读,地址:{}", filePath);
                return;
            }

            AssetManager am = AssetManager.class.newInstance();
            int result = (Integer) invoke(am, "addAssetPath", String.class, filePath);
            if (result == 0)
            {
                MyLog.w(TAG, "AssetManager.addAssetPath添加资源文件失败,返回结果:{},地址:{}", result, filePath);
                return;
            }

            Resources defaultResources = activityMaps.get(cxt);
            Configuration conf = defaultResources.getConfiguration();
            DisplayMetrics dm = defaultResources.getDisplayMetrics();
            skinResources = new SkinResources(am, dm, conf, defaultResources, cxt);
        }
        catch (Exception e)
        {
            MyLog.e(TAG, e);
        }
    }

    /**
     * 给界面添加更换皮肤转换的动画
     *
     * @param activity 要显示渐变动画的界面
     */
    private void skinChangeAnimation(Activity activity)
    {
        try
        {
            final ViewGroup decor = (ViewGroup) activity.getWindow()
                                                        .getDecorView();
            if (decor != null)
            {
                decor.setDrawingCacheEnabled(true);
                Bitmap temp = Bitmap.createBitmap(decor.getDrawingCache());
                decor.setDrawingCacheEnabled(false);
                ImageView iv = new ImageView(activity);
                iv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //消费在遮罩显示期间的事件
                    }
                });
                iv.setImageBitmap(temp);
                showAnimation(decor, iv, 800);
            }
        }
        catch (Exception e)
        {
            MyLog.w(TAG, e);
        }
    }

    /**
     * 在指定的DecorView上面显示一层遮罩,然后渐变消失
     *
     * @param decor     根View
     * @param coverView 覆盖在上面显示的View
     * @param duration  动画时间
     */
    private void showAnimation(final ViewGroup decor, final View coverView, final int duration)
    {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        decor.addView(coverView, lp);
        ViewPropertyAnimator.animate(coverView)
                            .alpha(0.01F)
                            .setDuration(duration)
                            .setListener(new AnimatorListenerAdapter()
                            {
                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    decor.removeView(coverView);
                                    isAnimationing = false;
                                }
                            })
                            .start();
        isAnimationing = true;
    }

    public synchronized void remove(Activity activity)
    {
        activityMaps.remove(activity);
    }

    /**
     * 当前是否正在显示切换皮肤动画
     *
     * @return 是 true
     */
    public boolean isAnimationing()
    {
        return isAnimationing;
    }
}
