package com.cantalou.test.indicatorviewpager;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CommonFragment extends Fragment
{

    private int i = 0;

    public CommonFragment(int i)
    {
        super();
        this.i = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        final View v = inflater.inflate(layout.fragment_main, container, false);
        container.post(new Runnable()
        {
            @Override
            public void run()
            {
                ImageView iv = (ImageView) v.findViewById(id.content);
                iv.setImageResource(i);
            }
        });

        return v;
    }

}
