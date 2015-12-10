package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.holder.AbstractHolder;

public class ViewHolder extends AbstractHolder
{

    protected int background;

    @Override
    public void reload(View view, Resources res)
    {
        if (background != 0)
        {
            view.setBackgroundDrawable(res.getDrawable(background));
        }
    }

    @Override
    public boolean parseAttr(Resources res, AttributeSet attrs)
    {
        background = getResourceId(attrs, "background");
        if (background != 0)
        {
            SkinManager.getInstance()
                       .registerDrawable(res, background);
        }
        return super.parseAttr(res, attrs) || background != 0;
    }

}