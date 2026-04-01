package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.BuildConfig;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.adapter.AboutAdapter;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.AboutImages;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.AboutName;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.AboutUrlContents;
import com.beckytech.og_artiiwwankutaa5ffaa.model.AboutModel;

import com.facebook.ads.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements AboutAdapter.OnLinkClicked {
    private final String TAG = "AboutActivity";
    private final AboutImages images = new AboutImages();
    private final AboutName name = new AboutName();
    private final AboutUrlContents urlContents = new AboutUrlContents();

    private InterstitialAd interstitialAd;
    private AdView adView;
    private List<AboutModel> modelList;
    private AboutModel pendingModel; // For high-revenue click handling

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize Ads immediately
        AudienceNetworkAds.initialize(this);
        loadBannerAd();
        loadInterstitialAd();

        // UI Setup
        findViewById(R.id.ib_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView title = findViewById(R.id.tv_title);
        title.setText("About Us");

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");

        TextView version = findViewById(R.id.version_tv);
        version.setText(String.format(Locale.ENGLISH, "Version: %s", BuildConfig.VERSION_NAME));

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(view -> shareApp());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_about);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();
        AboutAdapter adapter = new AboutAdapter(modelList, this);
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int i = 0; i < name.name.length; i++) {
            modelList.add(new AboutModel(images.images[i], name.name[i], urlContents.url[i]));
        }
    }

    @Override
    public void linkClicked(AboutModel model) {
        this.pendingModel = model;
        // High Revenue Strategy: Show ad when the user tries to leave the app via a link
        if (interstitialAd != null && interstitialAd.isAdLoaded() && !interstitialAd.isAdInvalidated()) {
            interstitialAd.show();
        } else {
            proceedToLink();
        }
    }

    private void proceedToLink() {
        if (pendingModel != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(pendingModel.getUrl()));
            startActivity(intent);
            pendingModel = null;
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "Check out " + getString(R.string.app_name) + " on Play Store!";
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void loadBannerAd() {
        // Move IDs to strings.xml for professional management
        adView = new AdView(this, getString(R.string.fb_banner_ads_about_us), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this, getString(R.string.fb_interstitial_ads_about_us));

        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDismissed(Ad ad) {
                // User closed the ad, now we let them go to the link
                proceedToLink();
                loadInterstitialAd(); // Preload for the next click
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Ad Error: " + adError.getErrorMessage());
                proceedToLink(); // Ad failed? Don't block the user, just let them go
            }

            @Override public void onAdLoaded(Ad ad) { Log.d(TAG, "Ad Loaded and ready"); }
            @Override public void onInterstitialDisplayed(Ad ad) {}
            @Override public void onAdClicked(Ad ad) {}
            @Override public void onLoggingImpression(Ad ad) {}
        };

        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(listener).build());
    }

    @Override
    protected void onDestroy() {
        if (adView != null) adView.destroy();
        if (interstitialAd != null) interstitialAd.destroy();
        super.onDestroy();
    }
}