package com.beckytech.og_artiiwwankutaa5ffaa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener, MoreAppsAdapter.MoreAppsClicked {
    public static int ADS_PER_ITEM = 3;
    private final MoreAppImages images = new MoreAppImages();
    private final MoreAppUrl url = new MoreAppUrl();
    private final MoreAppsName appsName = new MoreAppsName();
    private final String TAG = MainActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;
    private DrawerLayout drawerLayout;
    private List<Object> modelList;
    private List<Object> moreAppsModelList;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        callAds();

        AppRate.app_launched(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            MenuOptions(item);
            return true;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView_main_item);
        getData();
        Adapter adapter = new Adapter(modelList, this);
        recyclerView.setAdapter(adapter);
        addBanner(modelList);

        RecyclerView moreAppsRecyclerView = findViewById(R.id.moreAppsRecycler);
        getMoreApps();
        MoreAppsAdapter moreAppsAdapter = new MoreAppsAdapter(moreAppsModelList, this);
        moreAppsRecyclerView.setAdapter(moreAppsAdapter);
        addBanner(moreAppsModelList);
    }

    private void getMoreApps() {
        moreAppsModelList = new ArrayList<>();
        for (int i = 0; i < appsName.appNames.length; i++) {
            moreAppsModelList.add(new MoreAppsModel(appsName.appNames[i], url.url[i], images.images[i]));
        }
    }

    private void getData() {
        modelList = new ArrayList<>();
        for (int j = 0; j < TitleContents.title.length; j++) {
            modelList.add(new Model(TitleContents.title[j].substring(0, 1).toUpperCase() +
                    TitleContents.title[j].substring(1).toLowerCase(),
                    SubTitleContents.subTitle[j],
                    ContentStartPage.pageStart[j],
                    ContentEndPage.pageEnd[j]));
        }
    }

    private void addBanner(List<Object> list) {
        int j = 0;
        for (int i = ADS_PER_ITEM; i <= list.size(); i += ADS_PER_ITEM) {
            if (j % 3 == 0)
                // Banner
                adView = new AdView(this, "269798475392144_269799888725336", AdSize.BANNER_HEIGHT_50);
            else
                adView = new AdView(MainActivity.this, "269798475392144_282211424150849", AdSize.RECTANGLE_HEIGHT_250); // Rectangle
            list.add(i, adView);
            j++;
        }
        loadBanner(list);
    }

    private void loadBanner(List<Object> list) {
        loadBanner(ADS_PER_ITEM, list);
    }

    private void loadBanner(int adsPerItem, List<Object> list) {
        if (adsPerItem >= list.size()) {
            return;
        }
        Object items = list.get(adsPerItem);
        adView = (AdView) items;
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
//                Toast.makeText(MainActivity.this,"Error: " + adError.getErrorMessage(), Toast.LENGTH_LONG).show();
                loadBanner(adsPerItem + ADS_PER_ITEM, list);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                loadBanner(adsPerItem + ADS_PER_ITEM, list);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void MenuOptions(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.action_privacy) {
            startActivity(new Intent(this, PrivacyActivity.class));
        }
        if (item.getItemId() == R.id.action_about_us) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        if (item.getItemId() == R.id.action_rate) {
            String pkg = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
        }

        if (item.getItemId() == R.id.action_more_apps) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=6669279757479011928")));
        }

        if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "Download this app from Play store \n" + url);
            startActivity(Intent.createChooser(intent, "Choose to send"));
        }

        if (item.getItemId() == R.id.action_update) {
            SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            int lastVersion = pref.getInt("lastVersion", BuildConfig.VERSION_CODE);
            String url = "https://play.goSTogle.com/store/apps/details?id=" + getPackageName();
            if (lastVersion < BuildConfig.VERSION_CODE) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                Toast.makeText(this, "New update is available download it from play store!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No update available!", Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.action_exit) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Exit")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        System.exit(0);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setBackground(getResources().getDrawable(R.drawable.nav_header_bg, null))
                    .show();
        }
    }

    private void callAds() {
        AdView adView = new AdView(this, "269798475392144_269799888725336", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();

        interstitialAd = new InterstitialAd(this, "269798475392144_269800088725316");
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void showAdWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Check if interstitialAd has been loaded successfully
            if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (interstitialAd.isAdInvalidated()) {
                return;
            }
            // Show the ad
            interstitialAd.show();
        }, 1000 * 60 * 2); // Show the ad after 15 minutes
    }

    @Override
    public void onItemClicked(Model model) {
        showAdWithDelay();
        startActivity(new Intent(this, BookDetailActivity.class).putExtra("data", model));
    }

    @Override
    public void appClicked(MoreAppsModel model) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(model.getUrl()));
        showAdWithDelay();
        startActivity(intent);
    }
}