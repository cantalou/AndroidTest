package com.cantalou.skin.sparearray;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.TypedValue;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSpareArray extends LongSparseArray<Drawable.ConstantState>
{

    private SkinManager skinManager = SkinManager.getInstance();

    private LongSparseArray<Integer> resourceIdKeyMap = new LongSparseArray<Integer>();

    /**
     * Resources mDrawableCache
     */
    private LongSparseArray<Drawable.ConstantState> originalCache = new LongSparseArray<Drawable.ConstantState>();

    private TypedValue value = new TypedValue();

    @Override
    public Drawable.ConstantState get(long key)
    {
        Integer id = resourceIdKeyMap.get(key);
        if (id != null)
        {
            Resources res = skinManager.getCurrentSkinResources();
            Drawable dr = null;
            if (res instanceof ProxyResources)
            {
                res.getValue(id, value, true);
                dr = ((ProxyResources) res).proxyLoadDrawable(value, id);
            }
            else
            {
                dr = res.getDrawable(id);
            }
            if (dr != null)
            {
                return dr.getConstantState();
            }
        }
        return originalCache.get(key);
    }

    public void registerId(int id)
    {
        Resources res = skinManager.getDefaultResources();
        res.getValue(id, value, true);

        long key = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
        {
            boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
            key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
        }
        else
        {
            key = (((long) value.assetCookie) << 32) | value.data;
        }
        resourceIdKeyMap.put(key, id);
    }
}
