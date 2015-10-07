package com.wy.test.collection.copy;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;


import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.wy.test.HelloAndroidActivity;

@SuppressLint("NewApi")
public class QueueImplTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity>
{

    public QueueImplTest()
    {
        super(HelloAndroidActivity.class);
    }

    public QueueImplTest(Class<HelloAndroidActivity> activityClass)
    {
        super(HelloAndroidActivity.class);
    }

    public void test() throws InterruptedException
    {

        final CountDownLatch latch = new CountDownLatch(3);

        final int count = 30000;

        final int limit = 100;
        final boolean[] operIsIn = new boolean[limit];
        for (int j = 0; j < limit; j++)
        {
            operIsIn[j] = (System.nanoTime() & System.nanoTime() & 1) == 0;
        }

        new Thread(new Runnable()
        {
            public void run()
            {
                Deque<String> arrayImpl = new ArrayDeque<String>();
                long start = System.currentTimeMillis();
                for (int i = 0; i < count; i++)
                {
                    for (int j = 0; j < limit; j++)
                    {
                        if ((operIsIn[j] || arrayImpl.size() == 0) && arrayImpl.size() < limit)
                        {
                            arrayImpl.addLast("");
                        }
                        else
                        {
                            arrayImpl.removeFirst();
                        }
                    }
                }
                Log.i("", "ArrayDeque " + (System.currentTimeMillis() - start));
                latch.countDown();
            }
        }).start();

        new Thread(new Runnable()
        {
            public void run()
            {
                long start = System.currentTimeMillis();
                Queue<String> linkImpl = new LinkedList<String>();
                for (int i = 0; i < count; i++)
                {
                    for (int j = 0; j < limit; j++)
                    {
                        if ((operIsIn[j] || linkImpl.size() == 0) && linkImpl.size() < limit)
                        {
                            linkImpl.add("");
                        }
                        else
                        {
                            linkImpl.poll();
                        }
                    }
                }
                Log.i("", "LinkedList(q) " + (System.currentTimeMillis() - start));
                latch.countDown();
            }
        }).start();

        new Thread(new Runnable()
        {
            public void run()
            {
                long start = System.currentTimeMillis();
                Deque<String> linkImpl = new LinkedList<String>();
                for (int i = 0; i < count; i++)
                {
                    for (int j = 0; j < limit; j++)
                    {
                        if ((operIsIn[j] || linkImpl.size() == 0) && linkImpl.size() < limit)
                        {
                            linkImpl.addLast("");
                        }
                        else
                        {
                            linkImpl.removeFirst();
                        }
                    }
                }
                Log.i("", "LinkedList(d) " + (System.currentTimeMillis() - start));
                latch.countDown();
            }
        }).start();

        latch.await();
    }

}
