package com.cantalou.skin.sparearray;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.TypedValue;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListLongSpareArray extends LongSparseArray<ColorStateList>
{

    private SkinManager skinManager = SkinManager.getInstance();

    private LongSparseArray<Integer> resourceIdKeyMap = new LongSparseArray<Integer>();

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<ColorStateList> originalCache = new LongSparseArray<ColorStateList>();

    private TypedValue value = new TypedValue();

    public ColorStateListLongSpareArray(SkinManager skinManager, LongSparseArray<ColorStateList> originalCache)
    {
        this.skinManager = skinManager;
        this.originalCache = originalCache;
    }

    @Override
    public ColorStateList get(long key)
    {
        Integer id = resourceIdKeyMap.get(key);
        if (id != null)
        {
            Resources res = skinManager.getCurrentSkinResources();
            ColorStateList csl = null;
            if (res instanceof ProxyResources)
            {
                res.getValue(id, value, true);
                csl = ((ProxyResources) res).proxyLoadColorStateList(value, id);
            }
            else
            {
                csl = res.getColorStateList(id);
            }
            return csl;
        }
        return originalCache.get(key);
    }

    public void registerId(int id)
    {
        Resources res = skinManager.getDefaultResources();
        res.getValue(id, value, true);
        long key = (((long) value.assetCookie) << 32) | value.data;
        resourceIdKeyMap.put(key, id);
    }
}
