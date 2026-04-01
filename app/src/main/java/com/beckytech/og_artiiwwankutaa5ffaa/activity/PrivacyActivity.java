package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

public class PrivacyActivity extends AppCompatActivity {

    private AdView adView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        // Initialize Facebook Ads for the banner
        AudienceNetworkAds.initialize(this);
        loadBannerAd();

        findViewById(R.id.ib_back).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        ProgressBar progressBar = findViewById(R.id.progress_horizontal);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.privacy_title);

        WebView webView = findViewById(R.id.webView_privacy);
        setupWebView(webView, progressBar);

        // Load your policy URL
        webView.loadUrl("https://yoosaad.com/privacy/");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView webView, ProgressBar progressBar) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // Added for better modern web compatibility
        settings.setLoadsImagesAutomatically(true);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Professional fallback if the user is offline
                webView.loadUrl("file:///android_asset/error.html");
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBannerAd() {
        // High Revenue: Ensure the banner is visible while the user reads the policy
        adView = new AdView(this, getString(R.string.fb_banner_ads_privacy), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (adContainer != null) {
            adContainer.addView(adView);
            adView.loadAd();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}