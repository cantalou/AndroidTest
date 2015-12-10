package com.cantalou.skin.res;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.TypedValue;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSparseArray<E> extends LongSparseArray
{
    private LongSparseArray<Integer> mKeyIdMaps = new LongSparseArray();

    TypedValue value = new TypedValue();

    @Override
    public void put(long key, Object value)
    {
        super.put(key, value);
    }

    public void register(Resources resources, int resId)
    {
        resources.getValue(resId, value, true);
        if ((value.string != null) && (!value.string.toString()
                                                    .endsWith(".xml")))
        {
            long key = value.assetCookie << 32 | value.data;
            mKeyIdMaps.put(key, resId);
        }
    }

}
