package com.beckytech.og_artiiwwankutaa5ffaa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.activity.AboutActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.activity.BookDetailActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.activity.PrivacyActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.adapter.Adapter;
import com.beckytech.og_artiiwwankutaa5ffaa.adapter.MoreAppsAdapter;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentEndPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentStartPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.MoreAppImages;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.MoreAppUrl;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.MoreAppsName;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.SubTitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.TitleContents;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.beckytech.og_artiiwwankutaa5ffaa.model.MoreAppsModel;
import com.facebook.ads.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener, MoreAppsAdapter.MoreAppsClicked {

    public static final int ADS_PER_ITEM = 5; // Increased to 5 for better UX
    private final String TAG = "MainActivity";

    private InterstitialAd interstitialAd;
    private DrawerLayout drawerLayout;
    private Model pendingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        // 1. Initialize Ads
        AudienceNetworkAds.initialize(this);
        loadMainBanner();
        loadInterstitialAd();

        // 2. App Rater
        AppRate.app_launched(this);

        // 3. UI Setup (Toolbar & Drawer)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            menuOptions(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 4. Main Chapters List
        setupMainList();

        // 5. More Apps List
        setupMoreAppsList();
    }

    private void setupMainList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_main_item);
        List<Object> modelList = new ArrayList<>();

        // Load Data
        for (int j = 0; j < TitleContents.title.length; j++) {
            String formattedTitle = TitleContents.title[j].substring(0, 1).toUpperCase() +
                    TitleContents.title[j].substring(1).toLowerCase();
            modelList.add(new Model(formattedTitle, SubTitleContents.subTitle[j],
                    ContentStartPage.pageStart[j], ContentEndPage.pageEnd[j]));
        }

        // Inject In-Feed Ads
        injectAdsIntoList(modelList);

        Adapter adapter = new Adapter(modelList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupMoreAppsList() {
        RecyclerView moreAppsRecyclerView = findViewById(R.id.moreAppsRecycler);
        List<Object> moreAppsModelList = new ArrayList<>();

        MoreAppsName appsName = new MoreAppsName();
        MoreAppUrl url = new MoreAppUrl();
        MoreAppImages images = new MoreAppImages();

        for (int i = 0; i < appsName.appNames.length; i++) {
            moreAppsModelList.add(new MoreAppsModel(appsName.appNames[i], url.url[i], images.images[i]));
        }

        MoreAppsAdapter moreAppsAdapter = new MoreAppsAdapter(moreAppsModelList, this);
        moreAppsRecyclerView.setAdapter(moreAppsAdapter);
    }

    private void injectAdsIntoList(List<Object> list) {
        // Start from index 3, then every ADS_PER_ITEM
        for (int i = 3; i < list.size(); i += ADS_PER_ITEM) {
            AdSize size = (i % 2 == 0) ? AdSize.RECTANGLE_HEIGHT_250 : AdSize.BANNER_HEIGHT_50;
            AdView adView = new AdView(this, getString(R.string.fb_banner_ads_main), size);
            list.add(i, adView);
            adView.loadAd();
        }
    }

    private void loadMainBanner() {
        AdView adView = new AdView(this, getString(R.string.fb_banner_ads_main), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (adContainer != null) {
            adContainer.addView(adView);
            adView.loadAd();
        }
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this, getString(R.string.fb_interstitial_ads_main));
        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDismissed(Ad ad) {
                proceedToDetail();
                loadInterstitialAd(); // Reload for next time
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                proceedToDetail();
            }

            @Override public void onAdLoaded(Ad ad) { Log.d(TAG, "Interstitial Ready"); }
            @Override public void onInterstitialDisplayed(Ad ad) {}
            @Override public void onAdClicked(Ad ad) {}
            @Override public void onLoggingImpression(Ad ad) {}
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(listener).build());
    }

    @Override
    public void onItemClicked(Model model) {
        this.pendingModel = model;
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            proceedToDetail();
        }
    }

    private void proceedToDetail() {
        if (pendingModel != null) {
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("data", pendingModel);
            startActivity(intent);
            pendingModel = null;
        }
    }

    @Override
    public void appClicked(MoreAppsModel model) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl())));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void menuOptions(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.action_privacy) {
            startActivity(new Intent(this, PrivacyActivity.class));
        } else if (id == R.id.action_about_us) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_share) {
            shareApp();
        } else if (id == R.id.action_exit) {
            showExitDialog();
        } else if (id == R.id.action_rate) {
            rateApp();
        } else if (id == R.id.action_more_apps) {
            moreApps();
        } else if (id == R.id.action_update) {
            updateApp();
        }

    }

    private void updateApp() {
        // Dynamically gets the package name of the current app
        final String packageName = getPackageName();

        try {
            // market://details opens the app page directly in the Play Store app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Fallback to browser if Play Store app is disabled or missing
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void moreApps() {
        // Replace "Your_Developer_Name" with your actual Google Play Developer Name
        // or your Developer ID (e.g., 8504401574247581)
        final String developerId = "6669279757479011928";

        try {
            // Attempt to open the Play Store app to your developer page
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=" + developerId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Fallback: Open the developer page in the browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=" + developerId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void rateApp() {
        // Get your package name dynamically so this works for all 50 apps
        final String packageName = getPackageName();

        try {
            // Attempt to open the Play Store App
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Fallback: Open the Play Store in the Web Browser if the app isn't installed
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareMsg = "Check out " + getString(R.string.app_name) + " at: https://play.google.com/store/apps/details?id=" + getPackageName();
        intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void showExitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) interstitialAd.destroy();
        super.onDestroy();
    }
}