package com.qboxus.gograbdriver.activitiesandfragments.settingfragment;

import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.R;


public class PrivacyAndTermsF extends RootFragment implements View.OnClickListener {

    View view;

    ProgressBar progress_bar;
    WebView webView;
    TextView tvTitle;
    ImageView imgBack;
    String url="www.google.com";
    String title="";
    View.OnClickListener navClickListener;
    public PrivacyAndTermsF() {
    }

    public PrivacyAndTermsF(String title, String url, View.OnClickListener navClickListener) {
        this.url = url;
        this.title = title;
        this.navClickListener = navClickListener;
    }

    public PrivacyAndTermsF(String title, String URL) {
        this.url=URL;
        this.title=title;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_privacy_and_terms_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        if (view.getContext().getString(R.string.help).equalsIgnoreCase(title))
        {
            imgBack.setImageResource(R.drawable.ic_nav_menu);
            imgBack.setOnClickListener(navClickListener);
        }
        else
        {
            imgBack.setImageResource(R.drawable.ic_arrow_left);
            imgBack.setOnClickListener(this);
        }

    }

    private void initControl() {
        tvTitle =view.findViewById(R.id.tv_title);
        imgBack =view.findViewById(R.id.iv_back);
        webView=view.findViewById(R.id.webview);
        progress_bar=view.findViewById(R.id.progress_bar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpWebview();
            }
        },200);
    }

    private void setUpWebview() {
        tvTitle.setText(""+title);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if(progress>=80){
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
        }
    }
}