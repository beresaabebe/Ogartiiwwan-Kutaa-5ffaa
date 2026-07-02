package com.beckytech.og_artiiwwankutaa5ffaa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;

public class AppRate {
    private static final String APP_PNAME = BuildConfig.APPLICATION_ID;
    private final static int DAYS_UNTIL_PROMPT = 2;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("app_rater_prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dont_show_again", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        long launchCount = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launchCount);

        long dateFirstLaunch = prefs.getLong("date_first_launch", 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("date_first_launch", dateFirstLaunch);
        }

        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch + (long) DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showRateDialog(mContext, editor);
            }
        }
        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_rate_us, null);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView ratingText = view.findViewById(R.id.ratingText);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            int r = (int) rating;
            if (r == 1) ratingText.setText("Bad");
            else if (r == 2) ratingText.setText("Not good");
            else if (r == 3) ratingText.setText("Somewhat good");
            else if (r == 4) ratingText.setText("Very good");
            else if (r == 5) ratingText.setText("Excellent");
            else ratingText.setText("");
        });

        new MaterialAlertDialogBuilder(mContext)
                .setView(view)
                .setTitle("Enjoying " + mContext.getString(R.string.app_name) + "?")
                .setPositiveButton("Rate", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    if (rating >= 4) {
                        launchInAppReview(mContext);
                    } else {
                        Toast.makeText(mContext, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    }
                    if (editor != null) {
                        editor.putBoolean("dont_show_again", true);
                        editor.apply();
                    }
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private static void launchInAppReview(Context context) {
        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                if (context instanceof Activity) {
                    manager.launchReviewFlow((Activity) context, reviewInfo);
                }
            } else {
                // Fallback to Play Store
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME)));
                }
            }
        });
    }
}