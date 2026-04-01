package com.beckytech.og_artiiwwankutaa5ffaa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AppRate {
    // Pro-Tip: Use BuildConfig.APPLICATION_ID so you don't have to manually
    // change the package name for all 50 apps!
    private static final String APP_PNAME = BuildConfig.APPLICATION_ID;

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("app_rater_prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dont_show_again", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launchCount = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launchCount);

        // Get date of first launch
        long dateFirstLaunch = prefs.getLong("date_first_launch", 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("date_first_launch", dateFirstLaunch);
        }

        // Logic check for showing the prompt
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch +
                    (long) DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        String appName = mContext.getString(R.string.app_name);

        // Professional Material Dialog
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Rate " + appName)
                .setMessage("If you enjoy using " + appName + ", please take a moment to rate it on the Play Store. Your support helps us grow!")
                .setCancelable(false)
                .setPositiveButton("Rate Now", (dialog, which) -> {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + APP_PNAME)));
                    if (editor != null) {
                        editor.putBoolean("dont_show_again", true);
                        editor.apply();
                    }
                    dialog.dismiss();
                })
                .setNeutralButton("Remind Me Later", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("No, Thanks", (dialog, which) -> {
                    if (editor != null) {
                        editor.putBoolean("dont_show_again", true);
                        editor.apply();
                    }
                    dialog.dismiss();
                })
                .show();
    }
}