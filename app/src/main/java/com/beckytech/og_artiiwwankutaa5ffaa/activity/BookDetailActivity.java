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

public class BookDetailActivity extends AppCompatActivity {
    private final String TAG = "BookDetailActivity";
    private InterstitialAd interstitialAd;
    private AdView adView;
    private TextView subTitle, title;
    private int currentIndex;

    private int pendingIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        AudienceNetworkAds.initialize(this);
        loadBannerAd();
        loadInterstitialAd();

        findViewById(R.id.back_book_detail).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        Model model = (Model) intent.getSerializableExtra("data");

        title = findViewById(R.id.title_book_detail);
        subTitle = findViewById(R.id.sub_title_book_detail);

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

        int startPage = ContentStartPage.pageStart[index];
        int endPage = ContentEndPage.pageEnd[index];

        LinearLayout container = findViewById(R.id.pdfPagesContainer);
        container.removeAllViews(); // Clean the slate

        try {
            // 1. Copy Asset to a temp file (PdfRenderer needs a File Descriptor)
            java.io.File file = new java.io.File(getCacheDir(), "temp.pdf");
            if (!file.exists()) {
                java.io.InputStream in = getAssets().open("og5.pdf");
                java.io.OutputStream out = new java.io.FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); }
                in.close(); out.close();
            }

            // 2. Initialize Native Renderer
            android.graphics.pdf.PdfRenderer renderer = new android.graphics.pdf.PdfRenderer(
                    android.os.ParcelFileDescriptor.open(file, android.os.ParcelFileDescriptor.MODE_READ_ONLY));

            // 3. Render only the specific range
            for (int i = startPage; i <= endPage; i++) {
                android.graphics.pdf.PdfRenderer.Page page = renderer.openPage(i);

                // Create a bitmap for the page
                android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                        page.getWidth() * 2, page.getHeight() * 2, android.graphics.Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // Add to UI
                android.widget.ImageView imageView = new android.widget.ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 0, 0, 20);
                container.addView(imageView);

                page.close();
            }
            renderer.close();

        } catch (Exception e) {
            Log.e("RENDER_ERROR", "Failed to render native PDF", e);
        }
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
                loadInterstitialAd();
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