package com.radugunn.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.radugunn.app.R;
import com.radugunn.app.model.Cart;
import com.radugunn.app.model.CategoryList;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends BaseActivity {

    private static final String TAG = "NewsActivity";
    @BindView(R.id.wvNews)
    WebView wvNews;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    private boolean isfirstLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        //settvImage();
        settvTitle(getResources().getString(R.string.news));
        hideSearchNotification();
        setToolbarTheme();
        showBackButton();
        setScreenLayoutDirection();

        wvNews.getSettings().setLoadsImagesAutomatically(true);
        wvNews.getSettings().setJavaScriptEnabled(true);
        wvNews.getSettings().setDomStorageEnabled(true);
        wvNews.setWebViewClient(new WebViewClient());
        wvNews.setWebChromeClient(new WebChromeClient());
        wvNews.addJavascriptInterface(new NewsActivity.WebAppInterface(this), "Android");
        wvNews.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        wvNews.setWebViewClient(new myWebClient());
//        CookieManager.getInstance().setAcceptCookie(true);

        try {
            String url = "https://shop.radugann.com/novosti/";

            showProgress("");
            wvNews.loadUrl(url);


        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }


    }

    public class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            Log.e(TAG, "onPageStarted");
            dismissProgress();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
//            view.loadUrl(url);
            Log.e(TAG, "shouldOverrideUrlLoading: ");
            if (url.contains("http")) {
                view.loadUrl(url);
            }
            Log.e("URL GET = ", url);
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            Log.e(TAG, "onPageCommitVisible: " );
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            // progress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " );
            dismissProgress();

            wvNews.loadUrl("javascript:(function() { " +
                    "var head = document.getElementsByClassName('main-header-bar-wrap')[0].style.display='none'; " +
                    "})()");

            wvNews.loadUrl("javascript:(function() { " +
                    "var foot = document.getElementsByClassName('hfe-before-footer-wrap')[0].style.display='none'; " +
                    "})()");

            wvNews.loadUrl("javascript:(function() { " +
                    "var foot2 = document.getElementsByClassName('site-footer')[0].style.display='none'; " +
                    "})()");


        }
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(final String toast) {
            Log.e("Title is ", toast);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissProgress();
                    if (toast != null) {
                        CookieManager.getInstance().setAcceptCookie(true);
                        wvNews.loadUrl(toast);
                        wvNews.setVisibility(View.VISIBLE);
                        isfirstLoad = true;
                        dismissProgress();
                    }
                }
            });
        }
    }

}