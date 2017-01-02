package com.cantalou.test.animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.cantalou.test.R;

public class IcsAnimatorActivity extends Activity {

    @InjectView(R.id.size)
    Button size;
    @InjectView(R.id.wy)
    Button wy;
    @InjectView(R.id.alpha)
    Button alpha;
    @InjectView(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ics_animator);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.size, R.id.wy, R.id.alpha})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.size: {
                ObjectAnimator oa = ObjectAnimator.ofInt(image, "scaleX", 180, 500);
                oa.setDuration(2000);
                oa.start();
                break;
            }
            case R.id.wy: {
                ObjectAnimator oa = ObjectAnimator.ofInt(image, "transactionX", 0, 250);
                oa.setDuration(2000);
                oa.start();
                break;
            }
            case R.id.alpha: {
                ObjectAnimator oa = ObjectAnimator.ofFloat(image, "alpha", 1F, 0F);
                oa.setDuration(2000);
                oa.start();

                ObjectAnimator oa1 = ObjectAnimator.ofFloat(image, "alpha", 0F, 1F);
                oa1.setDuration(2000);
                oa1.setStartDelay(2000);
                break;
            }
        }
    }
}
