package com.cantalou.test.ui.listview;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cantalou.test.R.id;
import com.cantalou.test.R.layout;
import com.cantalou.android.util.CommonAdapter;

public class ListViewInScrollViewActivity extends Activity
{

    private LinearLayout ll;

    private ListView listView;

    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_listview_in_scrollview);

        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 10; i++)
        {
            data.add(Integer.toString(i));
        }

        listView = (ListView) findViewById(id.listview);
        listView.setAdapter(new CommonAdapter<String>(layout.hwbsa, data, this)
        {
            @Override
            public void handle(String data1)
            {
                TextView tv = findViewById(id.count);
                tv.setText(data1);
            }
        });
        setListViewHeightBasedOnChildren(listView);
        listView.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    sv.requestDisallowInterceptTouchEvent(false);
                }
                else
                {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
                return listView.onTouchEvent(event);
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_SHORT);
            }
        });

        sv = (ScrollView) findViewById(id.scrollView);
        sv.post(new Runnable()
        {
            @Override
            public void run()
            {
                sv.scrollTo(0, 0);
            }
        });

    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        if (listView == null)
        {
            return;
        }

        ListAdapter listAdapter = listView.getAdapter();

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount() / 2; i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    protected void onResume()
    {
        Log.i("", "onResume");
        super.onResume();
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
