package com.cantalou.skin.sparearray;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.TypedValue;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListSpareArray extends SparseArray<ColorStateList>
{

    private SkinManager skinManager = SkinManager.getInstance();

    private LongSparseArray<Integer> keyResourceIdMap = new LongSparseArray<Integer>();

    /**
     * Resources mColorStateListCache
     */
    private SparseArray<ColorStateList> originalCache = new SparseArray<ColorStateList>();

    private TypedValue value = new TypedValue();

    @Override
    public ColorStateList get(int key)
    {
        Integer id = keyResourceIdMap.get(key);
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
        if (keyResourceIdMap.indexOfValue(id) > -1)
        {
            return;
        }
        Resources res = skinManager.getDefaultResources();
        res.getValue(id, value, true);
        long key = (value.assetCookie << 24) | value.data;
        keyResourceIdMap.put(key, id);
    }
}
