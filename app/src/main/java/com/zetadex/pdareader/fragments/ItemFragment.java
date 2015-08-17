package com.zetadex.pdareader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zetadex.pdareader.ItemNewsActivity;
import com.zetadex.pdareader.R;

public class ItemFragment extends Fragment {

    public static ItemFragment newInstance(String url, String title) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemFragment() {
    }

    private WebView mWebView;
    private ContentLoadingProgressBar mProgress;
    private String mUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item, container, false);
        initViews(rootView);

        return rootView;

    }

    private void initViews(View rootView) {

        mProgress = (ContentLoadingProgressBar) rootView.findViewById(R.id.progress);

        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                ItemFragment.this.startActivity(intent);
            }
        });
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mUrl);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mUrl = getArguments().getString("url");
        String title = getArguments().getString("title");

        ((ItemNewsActivity) activity).onItemAttached(title);
    }

    public String getJavaScriptHack() {
        return "javascript:(function() { hide('header');hide('top-adbox');hide('footer');function hide(id){if (document.getElementById(id)){document.getElementById(id).style['display'] = 'none';}}})()";
    }


    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }

        public void onPageFinished(WebView webView, String url) {
            webView.loadUrl(getJavaScriptHack());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.setVisibility(View.VISIBLE);
            mProgress.hide();
        }

        public void onPageStarted(WebView webView, String url, Bitmap paramBitmap) {
            mProgress.show();
            webView.setVisibility(View.INVISIBLE);
            webView.getSettings().setLoadsImagesAutomatically(false);
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            webView.getContext().startActivity(intent);

            return true;
        }
    }

}
