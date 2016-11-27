package com.youibot.docrobo.robo.main.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.netease.nim.robo.R;
import com.youibot.docrobo.robo.main.model.MainTab;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoboFragment extends MainTabFragment implements View.OnClickListener{


    public RoboFragment() {
        // Required empty public constructor
        this.setContainerId(MainTab.ROBOR.fragmentId);
    }

    protected void onInit(){
        mWebView =(WebView)getView().findViewById(R.id.web_robo);


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mMyWebViewClient.onPageFinished(mWebView, "http://www.baidu.com/");
        mMyWebViewClient.shouldOverrideUrlLoading(mWebView, "http://www.baidu.com/");
        mMyWebViewClient.onPageFinished(mWebView, "http://www.baidu.com/");
        mWebView.setWebViewClient(mMyWebViewClient);
    }

    private WebView mWebView;
    MyWebViewClient mMyWebViewClient = new MyWebViewClient();

    public void onClick(View view){
        switch (view.getId()){

        }
    }


    class MyWebViewClient extends WebViewClient {

        ProgressDialog progressDialog;

        @Override
        public void onPageFinished(WebView view, String url) {//网页加载结束的时候
            //super.onPageFinished(view, url);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                mWebView.setEnabled(true);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { //网页加载时的连接的网址
            view.loadUrl(url);
            return false;
        }
    }
}
