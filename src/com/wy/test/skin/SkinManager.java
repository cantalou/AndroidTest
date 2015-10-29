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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wy.test.skin.resources.ProxyResources;
import com.wy.test.skin.resources.SkinResources;
import com.wy.test.util.ActivityStateUtil;
import com.wy.test.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wy.test.util.ReflectUtil.invoke;
import static com.wy.test.util.ReflectUtil.setValue;

public class SkinManager
{

    private static final String TAG = "SkinManager";

    /**
     * 存放资源包目录
     */
    private static final String SKIN_DIR = "/skin";

    /**
     * 默认皮肤
     */
    public static final String DEFAULT_SKIN_NAME = "defaultSkinName";

    /**
     * activity与资源map,用于保存activity改变皮肤前的resources
     */
    private ArrayList<Activity> activitys = new ArrayList<Activity>();

    /**
     * 以载入的资源
     */
    private HashMap<String, WeakReference<Resources>> cacheResources = new HashMap<String, WeakReference<Resources>>();

    /**
     * 当前是否正在切换资源
     */
    volatile boolean isChangingResource = false;

    /**
     * 默认资源
     */
    private Resources defaultResources;

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

    public void init(Context cxt)
    {
        registerViewFactory(cxt);
    }

    /**
     * 创建皮肤资源
     *
     * @param activity
     * @param skinName 皮肤资源文件名
     * @return
     * @throws Exception
     */
    private Resources createSkinResource(Activity activity, String skinName) throws Exception
    {
        Resources skinResources = null;

        File skinFile = new File(activity.getFilesDir() + File.separator + SKIN_DIR, skinName);
        if (!skinFile.exists())
        {
            throw new FileNotFoundException(skinFile + " not found");
        }

        AssetManager am = AssetManager.class.newInstance();
        invoke(am, "addAssetPath", new Class<?>[]{String.class}, skinFile.getAbsolutePath());
        skinResources = new SkinResources(am, defaultResources);

        return skinResources;
    }

    /**
     * 创建代理资源
     *
     * @param activity
     * @param skinName 资源名称
     * @return 代理Resources
     * @throws Exception
     */
    private Resources createProxyResource(Activity activity, String skinName) throws Exception
    {
        Resources res = null;
        WeakReference<Resources> resRef = cacheResources.get(skinName);
        if (resRef != null)
        {
            res = resRef.get();
            if (res != null)
            {
                return res;
            }
        }
        res = new ProxyResources(activity, createSkinResource(activity, skinName), defaultResources);
        cacheResources.put(skinName, new WeakReference<Resources>(res));
        return res;
    }

    /**
     * 将activity界面的资源替换成指定资源, activity界面
     *
     * @param activity 触发切换资源的Activity
     * @param skinName 资源文件名
     */
    public void changeResources(Activity activity, String skinName)
    {
        try
        {
            if (StringUtils.isBlank(skinName))
            {
                throw new IllegalArgumentException("illegal argument skinName is null");
            }

            if (defaultResources == null)
            {
                defaultResources = activity.getResources();
            }

            Resources res = null;
            if (DEFAULT_SKIN_NAME.equals(skinName))
            {
                res = defaultResources;
            }
            else
            {
                res = createProxyResource(activity, skinName);
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
     * 更换所有activity的皮肤资源
     */
    private void changeAll(Activity activity)
    {
        if (ActivityStateUtil.isDestroy(activity))
        {
            return;
        }

        // 在改变皮肤设置前,截图用于渐变动画显示
        skinChangeAnimation(activity);

        // 遍历所有的activity调用onSkinChange修改资源
        for (Activity a : activityMaps.keySet())
        {

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
                // Log.w(TAG, "文件不存在,地址:{}", filePath);
                return;
            }

            if (!file.canRead())
            {
                // Log.w(TAG, "文件不可读,地址:{}", filePath);
                return;
            }

            AssetManager am = AssetManager.class.newInstance();
            int result = (Integer) invoke(am, "addAssetPath", String.class, filePath);
            if (result == 0)
            {
                // Log.w(TAG,
                // "AssetManager.addAssetPath添加资源文件失败,返回结果:{},地址:{}", result,
                // filePath);
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
                        // 消费在遮罩显示期间的事件
                    }
                });
                iv.setImageBitmap(temp);
                showAnimation(decor, iv, 800);
            }
        }
        catch (Exception e)
        {
            // Log.w(TAG, e);
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
                                    isChangingResource = false;
                                }
                            })
                            .start();
        isChangingResource = true;
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
    public boolean isChangingResource()
    {
        return isChangingResource;
    }
}
