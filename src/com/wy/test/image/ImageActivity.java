package com.wy.test.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wy.test.R;
import com.wy.test.R.drawable;
import com.wy.test.R.id;

public class ImageActivity extends Activity
{

    private ViewGroup content;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_image);
        content = (ViewGroup) findViewById(id.content);
        content.setBackgroundColor(Color.rgb(0, 255, 0));

        LinearLayout ll;
        ImageView iv;
        TextView tv;
        LayoutParams lp;

        HashMap<ImageView.ScaleType, String> desc = new HashMap<ImageView.ScaleType, String>();
        desc.put(ImageView.ScaleType.MATRIX, "Scale using the image matrix when drawing");
        desc.put(ImageView.ScaleType.FIT_XY, "Scale in X and Y independently, so that src matches dst exactly. This may change the aspect ratio of the src.");
        desc.put(ImageView.ScaleType.FIT_START,
                 "Compute a scale that will maintain the original src aspect ratio, but will also ensure that src fits entirely inside dst. At least one axis (X or Y) will fit exactly. START aligns the result to the " +
                         "left and top edges of dst.");
        desc.put(ImageView.ScaleType.FIT_CENTER,
                 "Compute a scale that will maintain the original src aspect ratio, but will also ensure that src fits entirely inside dst. At least one axis (X or Y) will fit exactly. The result is centered inside " +
                         "dst.");
        desc.put(ImageView.ScaleType.FIT_END,
                 "Compute a scale that will maintain the original src aspect ratio, but will also ensure that src fits entirely inside dst. At least one axis (X or Y) will fit exactly. END aligns the result to the " +
                         "right and bottom edges of dst.");
        desc.put(ImageView.ScaleType.CENTER, "Center the image in the view, but perform no scaling");
        desc.put(ImageView.ScaleType.CENTER_CROP,
                 "Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width and height) of the image will be equal to or larger than the corresponding dimension of the view (minus " +
                         "padding). The image is then centered in the view");
        desc.put(ImageView.ScaleType.CENTER_INSIDE,
                 "Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width and height) of the image will be equal to or less than the corresponding dimension of the view (minus " +
                         "padding). The image is then centered in the view");

        for (ImageView.ScaleType scaleType : ImageView.ScaleType.values())
        {
            ll = new LinearLayout(this);
            ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            ((LayoutParams) ll.getLayoutParams()).setMargins(10, 60, 0, 0);
            ll.setOrientation(LinearLayout.VERTICAL);
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tv.setText(desc.get(scaleType) + "\n");
            ll.addView(tv);

            int[] ids = new int[]{drawable.vl, drawable.hl, drawable.vs, drawable.hs};
            for (int id : ids)
            {

                tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setText(scaleType.toString());
                ll.addView(tv);

                LinearLayout l = new LinearLayout(this);
                l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                l.setOrientation(LinearLayout.HORIZONTAL);

                iv = new ImageView(this);
                lp = new LayoutParams(100, 100);
                lp.setMargins(0, 0, 0, 5);
                iv.setLayoutParams(lp);
                iv.setScaleType(scaleType);
                iv.setImageResource(id);
                iv.setBackgroundColor(Color.rgb(255, 0, 0));
                l.addView(iv);

                iv = new ImageView(this);
                iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ((LayoutParams) iv.getLayoutParams()).setMargins(30, 0, 0, 0);
                iv.setImageResource(id);
                l.addView(iv);

                ll.addView(l);
            }

            content.addView(ll);
        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        //		ScreenShot.shoot(this,  new File(Environment.getExternalStorageDirectory().toString() + "/1.png"));
    }

    static class ScreenShot
    {

        private static Bitmap takeScreenShot(Activity activity)
        {
            // View是你需要截图的View
            View view = activity.getWindow()
                                .getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();

            // 获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow()
                    .getDecorView()
                    .getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            // 去掉标题栏
            Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, view.getWidth(), view.getHeight());
            view.destroyDrawingCache();
            return b;
        }

        private static void savePic(Bitmap b, File filePath)
        {
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(filePath);
                if (null != fos)
                {
                    b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public static void shoot(Activity a, File filePath)
        {
            if (filePath == null)
            {
                return;
            }
            if (!filePath.getParentFile()
                         .exists())
            {
                filePath.getParentFile()
                        .mkdirs();
            }
            ScreenShot.savePic(ScreenShot.takeScreenShot(a), filePath);
        }
    }
}
