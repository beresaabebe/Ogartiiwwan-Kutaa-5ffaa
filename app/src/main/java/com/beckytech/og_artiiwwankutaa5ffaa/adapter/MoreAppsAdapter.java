package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.model.MoreAppsModel;
import com.facebook.ads.AdView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MoreAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Object> modelList;
    private final MoreAppsClicked moreAppsClicked;
    private long lastClickTime = 0;

    private static final int ITEM_TYPE_APP = 0;
    private static final int ITEM_TYPE_BANNER = 1;

    public MoreAppsAdapter(List<Object> modelList, MoreAppsClicked moreAppsClicked) {
        this.modelList = modelList;
        this.moreAppsClicked = moreAppsClicked;
    }

    public interface MoreAppsClicked {
        void appClicked(MoreAppsModel model);
    }

    @Override
    public int getItemViewType(int position) {
        if (modelList.get(position) instanceof AdView) {
            return ITEM_TYPE_BANNER;
        }
        return ITEM_TYPE_APP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BANNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_banner_fb, parent, false);
            return new AdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moreapps_list_item, parent, false);
            return new AppsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppsViewHolder appsHolder) {
            MoreAppsModel model = (MoreAppsModel) modelList.get(position);

            appsHolder.appName.setText(model.getAppName());
            appsHolder.appImages.setImageResource(model.getImages());

            // 1. Click logic for the Button (Interception)
            appsHolder.downloadBtn.setOnClickListener(v -> {
                if (checkClickTime()) {
                    if (moreAppsClicked != null) moreAppsClicked.appClicked(model);
                }
            });

            // 2. Click logic for the Whole Card (Redundancy)
            appsHolder.itemView.setOnClickListener(v -> {
                if (checkClickTime()) {
                    if (moreAppsClicked != null) moreAppsClicked.appClicked(model);
                }
            });

        } else if (holder instanceof AdViewHolder adHolder) {
            if (modelList.get(position) instanceof AdView adView) {
                View container = adHolder.itemView;

                if (adView != null) {
                    container.setVisibility(View.VISIBLE);

                    // Robust Margin Fix to prevent casting crash
                    ViewGroup.LayoutParams baseParams = container.getLayoutParams();
                    if (baseParams instanceof ViewGroup.MarginLayoutParams marginParams) {
                        int marginSide = dpToPx(container, 12);
                        int marginTopBottom = dpToPx(container, 8);
                        marginParams.setMargins(marginSide, marginTopBottom, marginSide, marginTopBottom);
                        marginParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        container.setLayoutParams(marginParams);
                    }

                    ViewGroup adGroup = (ViewGroup) container;
                    if (adGroup.getChildCount() > 0) adGroup.removeAllViews();
                    if (adView.getParent() != null) ((ViewGroup) adView.getParent()).removeView(adView);
                    adGroup.addView(adView);
                } else {
                    container.setVisibility(View.GONE);
                }
            }
        }
    }

    // Professional Debounce Check
    private boolean checkClickTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }

    private int dpToPx(View view, int dp) {
        float density = view.getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public int getItemCount() {
        return modelList != null ? modelList.size() : 0;
    }

    public static class AppsViewHolder extends RecyclerView.ViewHolder {
        final ImageView appImages;
        final TextView appName;
        final MaterialButton downloadBtn; // Added this

        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            appImages = itemView.findViewById(R.id.more_apps_image);
            appName = itemView.findViewById(R.id.txt_app_name);
            downloadBtn = itemView.findViewById(R.id.download_btn); // Bind here
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}