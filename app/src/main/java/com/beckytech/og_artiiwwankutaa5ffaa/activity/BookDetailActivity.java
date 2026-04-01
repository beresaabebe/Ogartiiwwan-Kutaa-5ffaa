package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentEndPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentStartPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.SubTitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.TitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.facebook.ads.*;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private final String TAG = "BookDetailActivity";
    private InterstitialAd interstitialAd;
    private AdView adView;
    private PDFView pdfView;
    private TextView subTitle, title;
    private int currentIndex;

    // Revenue logic
    private int pendingIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Initialize Ads
        AudienceNetworkAds.initialize(this);
        loadBannerAd();
        loadInterstitialAd();

        findViewById(R.id.back_book_detail).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        Model model = (Model) intent.getSerializableExtra("data");

        title = findViewById(R.id.title_book_detail);
        subTitle = findViewById(R.id.sub_title_book_detail);
        pdfView = findViewById(R.id.pdfView);

        title.setSelected(true);
        subTitle.setSelected(true);

        if (model != null) {
            currentIndex = getIndex(model.getTitle());
            renderPdfChapter(currentIndex);
        }

        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton prevButton = findViewById(R.id.prevButton);
        ImageButton nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                handleChapterTransition(currentIndex - 1);
            } else {
                Toast.makeText(this, "Kun Boqonnaa jalqabaati!", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentIndex < TitleContents.title.length - 1) {
                handleChapterTransition(currentIndex + 1);
            } else {
                Toast.makeText(this, "Kun Boqonnaa xumuraati!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logic to show ad BETWEEN chapters (High Value)
    private void handleChapterTransition(int nextIdx) {
        this.pendingIndex = nextIdx;
        if (interstitialAd != null && interstitialAd.isAdLoaded() && !interstitialAd.isAdInvalidated()) {
            interstitialAd.show();
        } else {
            proceedToChapter();
        }
    }

    private void proceedToChapter() {
        if (pendingIndex != -1) {
            currentIndex = pendingIndex;
            renderPdfChapter(currentIndex);
            pendingIndex = -1;
        }
    }

    private int getIndex(String titleStr) {
        for (int i = 0; i < TitleContents.title.length; i++) {
            if (TitleContents.title[i].equalsIgnoreCase(titleStr)) return i;
        }
        return 0;
    }

    private void renderPdfChapter(int index) {
        title.setText(TitleContents.title[index]);
        subTitle.setText(SubTitleContents.subTitle[index]);

        int start = ContentStartPage.pageStart[index];
        int end = ContentEndPage.pageEnd[index];

        List<Integer> pages = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            pages.add(i);
        }

        int[] array = new int[pages.size()];
        for (int j = 0; j < pages.size(); j++) {
            array[j] = pages.get(j);
        }

        pdfView.fromAsset("og5.pdf")
                .pages(array)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .load();
    }

    private void loadBannerAd() {
        adView = new AdView(this, getString(R.string.fb_banner_ads_detail), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this, getString(R.string.fb_interstitial_ads_detail));
        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDismissed(Ad ad) {
                proceedToChapter();
                loadInterstitialAd(); // Load next one for the next chapter
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                proceedToChapter();
                loadInterstitialAd();
            }

            @Override public void onAdLoaded(Ad ad) { Log.d(TAG, "Ad Ready"); }
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