package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.adapter.ChapterPagerAdapter;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.SubTitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.TitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

public class BookDetailActivity extends AppCompatActivity {
    private final String TAG = "BookDetailActivity";
    private ViewPager2 viewPager;
    private TextView subTitle, title;
    private int currentIndex;
    private PdfRenderer pdfRenderer;
    private AdView bannerAdView;
    private InterstitialAd fbInterstitialAd;
    private boolean isUnityInterstitialLoaded = false;
    private boolean isUnityRewardedLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initViews();
        loadPdf();
        setupViewPager();
        loadBannerAd();
        loadFacebookInterstitial();
        loadUnityAds();

        Intent intent = getIntent();
        Model model = (Model) intent.getSerializableExtra("data");
        if (model != null) {
            currentIndex = getIndex(model.getTitle());
            viewPager.setCurrentItem(currentIndex, false);
            updateToolbar(currentIndex);
        }
    }

    private void initViews() {
        title = findViewById(R.id.title_book_detail);
        subTitle = findViewById(R.id.sub_title_book_detail);
        viewPager = findViewById(R.id.viewPagerDetail);
        findViewById(R.id.back_book_detail).setOnClickListener(v -> finish());
    }

    private void loadPdf() {
        try {
            File file = new File(getCacheDir(), "book.pdf");
            if (!file.exists()) {
                InputStream in = getAssets().open("og5.pdf");
                FileOutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); }
                in.close(); out.close();
            }
            pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
        } catch (Exception e) {
            Log.e(TAG, "PDF Load Error", e);
        }
    }

    private void setupViewPager() {
        ChapterPagerAdapter adapter = new ChapterPagerAdapter(pdfRenderer);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateToolbar(position);
                showRandomAd();
            }
        });
    }

    private void updateToolbar(int position) {
        title.setText(TitleContents.title[position]);
        subTitle.setText(SubTitleContents.subTitle[position]);
    }

    private int getIndex(String titleStr) {
        for (int i = 0; i < TitleContents.title.length; i++) {
            if (TitleContents.title[i].equalsIgnoreCase(titleStr)) return i;
        }
        return 0;
    }

    private void loadBannerAd() {
        bannerAdView = new AdView(this, getString(R.string.fb_banner_ads_detail), AdSize.BANNER_HEIGHT_50);
        LinearLayout container = findViewById(R.id.banner_container);
        if (container != null) {
            container.addView(bannerAdView);
            bannerAdView.loadAd();
        }
    }

    private void loadFacebookInterstitial() {
        fbInterstitialAd = new InterstitialAd(this, getString(R.string.fb_interstitial_ads_detail));
        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                fbInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(Ad ad) {}

            @Override
            public void onAdClicked(Ad ad) {}

            @Override
            public void onLoggingImpression(Ad ad) {}

            @Override
            public void onInterstitialDisplayed(Ad ad) {}

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (fbInterstitialAd != null) {
                    fbInterstitialAd.loadAd();
                }
            }
        };
        fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig().withAdListener(listener).build());
    }

    private void loadUnityAds() {
        UnityAds.load(getString(R.string.unity_interstitial_ads_unit_id), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                isUnityInterstitialLoaded = true;
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                isUnityInterstitialLoaded = false;
            }
        });

        UnityAds.load(getString(R.string.unity_rewarded_ads_unit_id), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                isUnityRewardedLoaded = true;
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                isUnityRewardedLoaded = false;
            }
        });
    }

    private void showRandomAd() {
        int rand = new Random().nextInt(3);
        if (rand == 0) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            }
        } else if (rand == 1) {
            if (isUnityInterstitialLoaded) {
                UnityAds.show(this, getString(R.string.unity_interstitial_ads_unit_id), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {}

                    @Override
                    public void onUnityAdsShowStart(String placementId) {}

                    @Override
                    public void onUnityAdsShowClick(String placementId) {}

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        loadUnityAds();
                    }
                });
            }
        } else {
            if (isUnityRewardedLoaded) {
                UnityAds.show(this, getString(R.string.unity_rewarded_ads_unit_id), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {}

                    @Override
                    public void onUnityAdsShowStart(String placementId) {}

                    @Override
                    public void onUnityAdsShowClick(String placementId) {}

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        loadUnityAds();
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (bannerAdView != null) bannerAdView.destroy();
        if (fbInterstitialAd != null) fbInterstitialAd.destroy();
        if (pdfRenderer != null) pdfRenderer.close();
        super.onDestroy();
    }
}
