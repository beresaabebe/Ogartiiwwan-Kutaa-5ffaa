package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> modelList;
    private final OnItemClickedListener itemClickedListener;
    private long lastClickTime = 0;

    private static final int ITEM_TYPE_BOOK = 0;
    private static final int ITEM_TYPE_BANNER = 1;

    public Adapter(List<Object> modelList, OnItemClickedListener itemClickedListener) {
        this.modelList = modelList;
        this.itemClickedListener = itemClickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(Model model);
    }

    @Override
    public int getItemViewType(int position) {
        if (modelList.get(position) instanceof AdView) {
            return ITEM_TYPE_BANNER;
        }
        return ITEM_TYPE_BOOK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BANNER) {
            // Using the collapsed XML we created earlier (visibility:gone)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_banner_fb, parent, false);
            return new AdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new MainViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainViewHolder mainHolder) {
            Model model = (Model) modelList.get(position);
            mainHolder.title.setText(model.getTitle());
            mainHolder.subTitle.setText(model.getSubtitle());

            mainHolder.itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > 1000) {
                    lastClickTime = currentTime;
                    if (itemClickedListener != null) {
                        itemClickedListener.onItemClicked(model);
                    }
                }
            });

        }
        else if (holder instanceof AdViewHolder adHolder) {
            if (modelList.get(position) instanceof AdView adView) {
                // The itemView IS the LinearLayout from ad_banner_fb.xml
                View container = adHolder.itemView;

                if (adView != null) {
                    container.setVisibility(View.VISIBLE);

                    // FIX: Use MarginLayoutParams to avoid the ClassCastException
                    ViewGroup.LayoutParams baseParams = container.getLayoutParams();
                    if (baseParams instanceof ViewGroup.MarginLayoutParams marginParams) {
                        int marginSide = dpToPx(container, 12);
                        int marginTopBottom = dpToPx(container, 8);
                        marginParams.setMargins(marginSide, marginTopBottom, marginSide, marginTopBottom);

                        // Set height back to wrap_content in case it was collapsed to 0
                        marginParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        container.setLayoutParams(marginParams);
                    }

                    // Standard cleaning logic
                    ViewGroup adGroup = (ViewGroup) container;
                    if (adGroup.getChildCount() > 0) {
                        adGroup.removeAllViews();
                    }

                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    adGroup.addView(adView);
                } else {
                    // COLLAPSE logic if ad is null
                    container.setVisibility(View.GONE);
                    container.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }
        }
    }

    // Helper to convert DP to Pixels for dynamic margins
    private int dpToPx(View view, int dp) {
        float density = view.getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public int getItemCount() {
        return modelList != null ? modelList.size() : 0;
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        final TextView title, subTitle;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_main_item);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}