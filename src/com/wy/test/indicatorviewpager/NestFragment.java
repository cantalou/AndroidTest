package com.wy.test.indicatorviewpager;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.viewpagerindicator.TabPageIndicator;
import com.wy.test.R.drawable;
import com.wy.test.R.id;
import com.wy.test.R.layout;

public class NestFragment extends Fragment
{

    private static final String TAG = "NestFragment";

    private ViewPager viewPager;

    //	private TabPageIndicator indicator;

    private FragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        init();

        View v = inflater.inflate(layout.indicator_fragment, container, false);

        adapter = new CustFragmentPagerAdapter(getActivity().getSupportFragmentManager());

        viewPager = (ViewPager) v.findViewById(id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        //		indicator = (TabPageIndicator) v.findViewById(id.indicator);
        //		indicator.setViewPager(viewPager);

        return v;
    }

    List<Integer> list = new ArrayList<Integer>(4);

    private void init()
    {
        list.add(drawable.i1);
        list.add(drawable.i2);
        list.add(drawable.i3);
        list.add(drawable.i4);
    }

    class CustFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public CustFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.i(TAG, position + "");
            return new CommonFragment(list.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return list.get(position)
                       .toString();
        }
    }

}
