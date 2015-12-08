package com.cantalou.test.ui.slidingmenu;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LeftFragment extends Fragment
{

    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(layout.fragment_left, null);

        lv = (ListView) v.findViewById(id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,
                                                                new String[]{"item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item", "item",
                                                                             "item", "item", "item"});
        lv.setAdapter(adapter);
        return v;
    }

}