package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.holder.ViewHolder;

public class ImageViewHolder extends ViewHolder
{

    protected int src;

    @SuppressWarnings("deprecation")
    @Override
    public void reload(View view, Resources res)
    {
        super.reload(view, res);
        if (src != 0)
        {
            ((ImageView) view).setImageDrawable(res.getDrawable(src));
        }
    }

    @Override
    public boolean parseAttr(Resources res, AttributeSet attrs)
    {
        src = getResourceId(attrs, "src");
        if (src != 0)
        {
            SkinManager.getInstance()
                       .registerDrawable(res, src);
        }
        return super.parseAttr(res, attrs) || src != 0;
    }
}
