package com.wy.test.webview;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.wy.test.R;

public class WebViewActivity extends Activity
{

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/test.html");
    }
}
