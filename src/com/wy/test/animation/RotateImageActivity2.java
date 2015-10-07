package com.wy.test.animation;

import android.app.Activity;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wy.test.R;
import com.wy.test.R.drawable;
import com.wy.test.R.id;
import com.wy.test.R.layout;

public final class RotateImageActivity2 extends Activity
{

    private ImageView v1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.rotate_image2);
        v1 = (ImageView) findViewById(id.image1);
        v1.setBackgroundResource(drawable.round);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable.f);
        Matrix m = new Matrix();
        m.setRotate(45);
        Bitmap transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2, m, true);
        v1.setImageBitmap(transformed);
        bitmap.recycle();
    }

    //	int delat = 0;
    //
    //	Runnable task = new Runnable() {
    //		public void run() {
    //			getWindow().getDecorView().postDelayed(task, 2000);
    //			Rotate3d leftAnimation = new Rotate3d(0, -360, 0, 0, v1.getWidth() / 2, v1.getHeight() / 2);
    //			leftAnimation.setDuration(2000);
    //			v1.startAnimation(leftAnimation);
    //		}
    //	};
    //
    //	@Override
    //	public void onWindowFocusChanged(boolean hasFocus) {
    //		super.onWindowFocusChanged(hasFocus);
    //		// getWindow().getDecorView().post(task);
    //		// Animation animation = AnimationUtils.loadAnimation(this,
    //		// R.anim.myanim);
    //		// v1.startAnimation(animation);
    //	}

    //	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
    //
    //		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    //		Canvas canvas = new Canvas(output);
    //		canvas.drawARGB(0, 0, 0, 0);
    //
    //		final Paint paint = new Paint();
    //		paint.setAntiAlias(true);
    //		paint.setColor(0xff424242);
    //		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
    //
    //		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    //		final RectF rectF = new RectF(rect);
    //		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    //		canvas.drawBitmap(bitmap, rect, rect, paint);
    //
    //		return output;
    //	}

}
