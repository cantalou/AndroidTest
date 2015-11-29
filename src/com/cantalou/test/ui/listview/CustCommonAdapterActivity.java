package com.cantalou.test.ui.listview;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;
import com.cantalou.android.util.CommonAdapter;

public class CustCommonAdapterActivity extends Activity
{

    static class Item
    {
        public String a;
    }

    private ListView lv;

    private ArrayList<Item> data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_common_adapter_listview);
        lv = (ListView) findViewById(id.listview);

        data = new ArrayList<Item>();
        for (int i = 0; i < 100; i++)
        {
            Item item = new Item();
            item.a = String.valueOf(i);
            data.add(item);
        }

        final HashMap<View, Integer> views = new HashMap<View, Integer>();

        lv.setAdapter(new CommonAdapter<Item>(layout.item, data, this)
        {
            @Override
            public void handle(Item data1)
            {
                TextView tv = findViewById(id.title);
                if (!views.containsKey(currentView))
                {
                    views.put(currentView, currentPosition);
                    tv.setText(currentPosition + " " + currentView.toString() + " false");
                }
                else
                {
                    tv.setText(currentPosition + " " + currentView.toString() + " true " + views.get(currentView));
                }

            }
        });
    }

}
