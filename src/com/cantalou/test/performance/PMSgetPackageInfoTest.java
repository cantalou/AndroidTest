package com.cantalou.test.performance;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.cantalou.android.util.Log;

/**
 * @author cantalou
 * @date 2017-09-02 17:20
 */
public class PMSgetPackageInfoTest extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int count = 100;
        double ipcTime = 0, parseFileTime = 0;
        String packageName = getPackageName();
        PackageManager pm = getPackageManager();
        PackageInfo info1 = null;
        try {
            for (int i = 0; i < count; i++) {
                long start = System.currentTimeMillis();
                info1 = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                ipcTime += System.currentTimeMillis() - start;
            }

            for (int i = 0; i < count; i++) {
                long start = System.currentTimeMillis();
                pm.getPackageArchiveInfo(info1.applicationInfo.sourceDir, PackageManager.GET_ACTIVITIES);
                parseFileTime += System.currentTimeMillis() - start;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("ipcTime:{},parseFileTime:{}",ipcTime,parseFileTime );
    }
}
