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
import androidx.annotation.NonNull;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements AboutAdapter.OnLinkClicked {
    private final String TAG = "AboutActivity";
    private final AboutImages images = new AboutImages();
    private final AboutName name = new AboutName();
    private final AboutUrlContents urlContents = new AboutUrlContents();
    private InterstitialAd mInterstitialAd;
    private List<AboutModel> modelList;
    private AboutModel pendingModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        loadBannerAd();
        loadInterstitialAd();
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tv_title)).setText("About Us");
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");
        ((TextView) findViewById(R.id.version_tv)).setText(String.format(Locale.ENGLISH, "Version: %s", BuildConfig.VERSION_NAME));
        findViewById(R.id.imageView).setOnClickListener(view -> shareApp());
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_about);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();
        recyclerView.setAdapter(new AboutAdapter(modelList, this));
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
        if (mInterstitialAd != null) mInterstitialAd.show(this);
        else proceedToLink();
    }

    private void proceedToLink() {
        if (pendingModel != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pendingModel.getUrl())));
            pendingModel = null;
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out " + getString(R.string.app_name) + " on Play Store!");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void loadBannerAd() {
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.google_banner_ads_unit_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (adContainer != null) {
            adContainer.addView(adView);
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    private void loadInterstitialAd() {
        InterstitialAd.load(this, getString(R.string.google_interstitial_ads_unit_id),
                new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                proceedToLink();
                                loadInterstitialAd();
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { mInterstitialAd = null; }
                });
    }
}