package com.cantalou.test.pageturn;

import android.app.Activity;
import android.os.Bundle;

import com.cantalou.test.R;


import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

public class PageTurnActivity extends Activity
{
    /**
     * Called when the activity is first created.
     */
    private PageWidget mPageWidget;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_page_turn);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            final FrameLayout decor = ((FrameLayout) getWindow().getDecorView());

            if (decor.getChildCount() == 1)
            {
                final View front = getLayoutInflater().inflate(R.layout.view_page_turn_front, null);
                decor.addView(front);
                decor.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Bitmap mNextPageBitmap = Bitmap.createBitmap(front.getWidth(), front.getHeight(), Bitmap.Config.ARGB_8888);

                        front.setDrawingCacheEnabled(true);
                        Bitmap mCurPageBitmap = Bitmap.createBitmap(front.getDrawingCache());
                        front.setDrawingCacheEnabled(false);

                        mPageWidget = new PageWidget(PageTurnActivity.this, front.getWidth(), front.getHeight());
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                        decor.removeView(front);
                        decor.addView(mPageWidget);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.skin, menu);
        return true;
    }
}
