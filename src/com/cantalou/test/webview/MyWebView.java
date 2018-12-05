package com.cantalou.test.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Scroller;

/**
 * Project Name: m4399_Forums
 * File Name:    MyWebView.java
 * ClassName:    MyWebView
 *
 * Description: 自定义WebView.
 *
 * @author jia chen
 * @date 2014年08月22日 上午11:45
 *
 * Copyright (c) 2014年, Network CO.ltd. All Rights Reserved.
 */
@SuppressLint("SetJavaScriptEnabled")
public class MyWebView extends WebView
{
    public static final String TAG = "MyWebView";

    /**
     * The m context.
     */
    private Context mContext;

    /**
     * 设置webView加载监听.
     */
    private OnWebViewPageListener mWebViewPageListener;

    /**
     * 设置webView滑动监听.
     */

    /**
     * The m scroller.
     */
    private Scroller mScroller;

    /**
     * 是否显示loading界面（默认开启）.
     */
    private boolean isShowLoading = true;

    /**
     * js交互功能名.
     */
    private String mJSFunctionName; // WAP传来的可调用的JS方法名

    /**
     * The m web view loaded count.
     */
    private int mWebViewLoadedCount = 0;

    /**
     * 默认UA.
     */
    private String mDefaultUA;

    /**
     * 网页设置.
     */
    private WebSettings mSettings;


    /**
     * 当前webView是否已调用destory
     */
    private volatile boolean mDestroyed;


    public MyWebView(Context context)
    {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mDestroyed = false;
        mContext = context;
        mScroller = new Scroller(context);
        if (isInEditMode())
        {
            return;
        }
        init();
    }

    /**
     * Sets the sets the ua.
     *
     * @param isDefaultUA the new sets the ua
     */
    public void setDefaultUA(boolean isDefaultUA)
    {
        if (isDefaultUA)
        {
            mSettings.setUserAgentString(mDefaultUA);
        }
    }

    /**
     * Checks if is show loading.
     *
     * @param showLoading the show loading
     */
    public void setShowLoading(boolean showLoading)
    {
        isShowLoading = showLoading;
    }

    /**
     * 增加webView设置.
     */
    public void addWebViewClient()
    {
        this.setWebViewClient(new MyWebViewClient());
    }

    /**
     * 设置webView加载监听.
     *
     * @param listener 监听对象
     */
    public void setWebViewPageListener(OnWebViewPageListener listener)
    {
        mWebViewPageListener = listener;
    }

    /**
     * 初始化.
     */
    private void init()
    {
        webSetting();
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setScrollbarFadingEnabled(true);
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    /**
     * webView通用设置.
     */
    private void webSetting()
    {
        mSettings = getSettings();
        mSettings.setJavaScriptEnabled(true);
        mSettings.setDomStorageEnabled(true);
        mSettings.setDefaultTextEncodingName("utf-8");
        mSettings.setNeedInitialFocus(false);
        mSettings.setSaveFormData(true);
        mSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mSettings.setAllowFileAccess(true);
        mSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSettings.setUseWideViewPort(true);
        mDefaultUA = mSettings.getUserAgentString();
    }



    @Override
    public void destroy()
    {
        mDestroyed = true;
        mScroller = null;
        mWebViewPageListener = null;
        MyWebView.this.removeAllViews();

        super.destroy();
    }

    /**
     * 自定义webViewClient.
     */
    private class MyWebViewClient extends WebViewClient
    {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {

            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
        }

        /**
         * webView开始加载页面.
         *
         * @param view    webView
         * @param url     当前加载链接
         * @param favicon favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {

        }

        @Override
        public void onLoadResource(WebView view, String url)
        {
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
    }


    @Override
    public void computeScroll()
    {
        // 先判断mScroller滚动是否完成
        if (mScroller != null && mScroller.computeScrollOffset())
        {
            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * WebView页面加载监听.
     */
    public interface OnWebViewPageListener
    {

        void onWebViewPageStart(WebView view, String url, Bitmap favicon);

        void onWebViewPageFinished(WebView view, String url);

        void onWebViewReceivedError(WebView view, int errorCode, String description, String failingUrl);

        boolean shouldOverrideUrlLoading(WebView view, String url);

        void onLoadResource(WebView view, String url);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (!this.isFocusable())
                {
                    this.requestFocus();
                }
            }
        }
        return super.onTouchEvent(event);
    }


}
