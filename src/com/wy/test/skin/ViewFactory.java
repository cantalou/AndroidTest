package com.wy.test.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wy.test.skin.holder.AttrHolder;
import com.wy.test.skin.holder.ImageViewHolder;
import com.wy.test.skin.holder.TextViewHolder;
import com.wy.test.skin.holder.ViewHolder;

import java.util.HashMap;

public class ViewFactory implements Factory
{

    private final String[] sClassPrefixList = {"android.widget.", "android.webkit.", "android.app."};

    private final HashMap<Class<?>, AttrHolder> viewAttrHolder = new HashMap<Class<?>, AttrHolder>()
    {
        {
            put(View.class, new ViewHolder());
            put(TextView.class, new TextViewHolder());
            put(ImageView.class, new ImageViewHolder());
            put(ListView.class, new ImageViewHolder());
        }
    };

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs)
    {
        View view = null;
        try
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (-1 == name.indexOf('.'))
            {
                for (String prefix : sClassPrefixList)
                {
                    try
                    {
                        view = inflater.createView(name, prefix, attrs);
                        if (view != null)
                        {
                            return view;
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        // In this case we want to let the base class take a
                        // crack at it.
                    }
                }
            }
            else
            {
                view = inflater.createView(name, null, attrs);
            }

            if (view == null)
            {
                return null;
            }

            getHolder(view.getClass()).parse(view, attrs);

            return view;

        }
        catch (InflateException e)
        {
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        }
        catch (Exception e)
        {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        }
    }

    private AttrHolder getHolder(Class<?> clazz)
    {
        if (clazz == null)
        {
            throw new IllegalStateException("not a subclass of View");
        }
        AttrHolder attrHolder = viewAttrHolder.get(clazz);
        if (attrHolder != null)
        {
            return attrHolder;
        }
        else
        {
            return getHolder(clazz.getSuperclass());
        }
    }
}