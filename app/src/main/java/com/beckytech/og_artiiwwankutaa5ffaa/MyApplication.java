package com.beckytech.og_artiiwwankutaa5ffaa;

import android.app.Application;
import com.facebook.ads.AudienceNetworkAds;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Facebook Audience Network
        AudienceNetworkAds.initialize(this);

        // Initialize Unity Ads using unity_game_id
        UnityAds.initialize(this, getString(R.string.unity_game_id), false, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                // Unity Ads initialization successful
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                // Unity Ads initialization failed
            }
        });
    }
}
