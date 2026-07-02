package com.beckytech.og_artiiwwankutaa5ffaa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickedListener, MoreAppsAdapter.MoreAppsClicked {

    public static final int ADS_PER_ITEM = 5;
    private InterstitialAd mInterstitialAd;
    private DrawerLayout drawerLayout;
    private Model pendingModel;
    private AppUpdateManager appUpdateManager;
    private static final int UPDATE_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        loadBannerAd();
        loadInterstitialAd();
        checkForUpdate();

        AppRate.app_launched(this);

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

        setupMainList();
        setupMoreAppsList();
    }

    private void checkForUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupMainList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_main_item);
        List<Object> modelList = new ArrayList<>();
        for (int j = 0; j < TitleContents.title.length; j++) {
            String formattedTitle = TitleContents.title[j].substring(0, 1).toUpperCase() +
                    TitleContents.title[j].substring(1).toLowerCase();
            modelList.add(new Model(formattedTitle, SubTitleContents.subTitle[j],
                    ContentStartPage.pageStart[j], ContentEndPage.pageEnd[j]));
        }
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
        moreAppsRecyclerView.setAdapter(new MoreAppsAdapter(moreAppsModelList, this));
    }

    private void injectAdsIntoList(List<Object> list) {
        for (int i = 3; i < list.size(); i += ADS_PER_ITEM) {
            AdView adView = new AdView(this);
            adView.setAdUnitId(getString(R.string.google_banner_ads_unit_id));
            adView.setAdSize((i % 2 == 0) ? AdSize.MEDIUM_RECTANGLE : AdSize.BANNER);
            list.add(i, adView);
            adView.loadAd(new AdRequest.Builder().build());
        }
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
                    public void onAdLoaded(@androidx.annotation.NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                proceedToDetail();
                                loadInterstitialAd();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@androidx.annotation.NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onItemClicked(Model model) {
        this.pendingModel = model;
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
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
            AppRate.showRateDialog(this, null);
        } else if (id == R.id.action_more_apps) {
            moreApps();
        } else if (id == R.id.action_update) {
            checkForUpdate();
        }
    }

    private void moreApps() {
        final String developerId = "6669279757479011928";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=" + developerId)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=" + developerId)));
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out " + getString(R.string.app_name) + " at: https://play.google.com/store/apps/details?id=" + getPackageName());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("UPDATE", "Update flow failed! Result code: " + resultCode);
            }
        }
    }
}