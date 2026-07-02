package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.adapter.ChapterPagerAdapter;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.SubTitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.TitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
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
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initViews();
        loadPdf();
        setupViewPager();
        loadCollapsibleBanner();
        loadRewardedAds();

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

    private void loadCollapsibleBanner() {
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.google_banner_ads_unit_id));
        adView.setAdSize(AdSize.BANNER);
        
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        
        LinearLayout container = findViewById(R.id.banner_container);
        container.addView(adView);
        adView.loadAd(adRequest);
    }

    private void loadRewardedAds() {
        RewardedAd.load(this, getString(R.string.google_rewarded_ads_unit_id),
                new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) { rewardedAd = ad; }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) { rewardedAd = null; }
                });

        RewardedInterstitialAd.load(this, getString(R.string.google_rewarded_interstitial_ads_unit_id),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) { rewardedInterstitialAd = ad; }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) { rewardedInterstitialAd = null; }
                });
    }

    private void showRandomAd() {
        int rand = new Random().nextInt(10);
        if (rand % 2 == 0) {
            if (rewardedAd != null) {
                rewardedAd.show(this, rewardItem -> loadRewardedAds());
            }
        } else {
            if (rewardedInterstitialAd != null) {
                rewardedInterstitialAd.show(this, rewardItem -> loadRewardedAds());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (pdfRenderer != null) pdfRenderer.close();
        super.onDestroy();
    }
}