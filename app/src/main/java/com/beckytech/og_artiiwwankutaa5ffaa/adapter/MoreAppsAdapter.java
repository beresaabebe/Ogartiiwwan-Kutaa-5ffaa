package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.MainActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.model.MoreAppsModel;
import com.facebook.ads.AdView;

import java.util.List;

public class MoreAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Object> modelList;
    private final MoreAppsClicked moreAppsClicked;

    private final int ITEM_TYPE_BOOK = 0;
    private final int ITEM_TYPE_BANNER = 1;

    public MoreAppsAdapter(List<Object> modelList, MoreAppsClicked moreAppsClicked) {
        this.modelList = modelList;
        this.moreAppsClicked = moreAppsClicked;
    }

    public interface MoreAppsClicked {
        public void appClicked(MoreAppsModel model);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_BOOK:
                return new AppsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.moreapps_list_item, parent, false));
            case ITEM_TYPE_BANNER: default:
                return new Adapter.AdviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_banner_fb, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_BOOK:
                if (modelList.get(position) instanceof MoreAppsModel) {
                    AppsViewHolder appsViewHolder = (AppsViewHolder) holder;
                    MoreAppsModel model = (MoreAppsModel) modelList.get(position);
                    appsViewHolder.appName.setText(model.getAppName());
                    appsViewHolder.appImages.setImageResource(model.getImages());
                    appsViewHolder.itemView.setOnClickListener(v -> moreAppsClicked.appClicked(model));
                }
                break;
            case ITEM_TYPE_BANNER: default:
                if (modelList.get(position) instanceof AdView) {
                    Adapter.AdviewHolder adviewHolder = (Adapter.AdviewHolder) holder;
                    AdView adView = (AdView) modelList.get(position);
                    ViewGroup viewGroup = (ViewGroup) adviewHolder.itemView;
                    if (viewGroup.getChildCount() > 0) {
                        viewGroup.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup)adView.getParent()).removeView(adView);
                    }
                    viewGroup.addView(adView);
                }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || modelList.get(position) instanceof MoreAppsModel) return ITEM_TYPE_BOOK;
        else return (position % MainActivity.ADS_PER_ITEM == 0) ? ITEM_TYPE_BANNER : ITEM_TYPE_BOOK;
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class AppsViewHolder extends RecyclerView.ViewHolder {
        ImageView appImages;
        TextView appName;
        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            appImages = itemView.findViewById(R.id.more_apps_image);
            appName = itemView.findViewById(R.id.txt_app_name);
        }
    }
}
