package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.MainActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.model.Model;
import com.facebook.ads.AdView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> modelList;
    private final OnItemClickedListener itemClickedListener;

    private final int ITEM_TYPE_BOOK = 0;
    private final int ITEM_TYPE_BANNER = 1;

    public Adapter(List<Object> modelList, OnItemClickedListener itemClickedListener) {
        this.modelList = modelList;
        this.itemClickedListener = itemClickedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_BOOK: return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
            case ITEM_TYPE_BANNER: default: return new AdviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_banner_fb, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_BOOK:
                if (modelList.get(position) instanceof Model) {
                    MainViewHolder mainViewHolder = (MainViewHolder) holder;
                    Model model = (Model) modelList.get(position);
                    mainViewHolder.title.setText(model.getTitle());
                    mainViewHolder.subTitle.setText(model.getSubtitle());
                    mainViewHolder.itemView.setOnClickListener(v -> itemClickedListener.onItemClicked(model));
                }
                break;
            case ITEM_TYPE_BANNER: default:
                if (modelList.get(position) instanceof AdView) {
                    AdviewHolder adviewHolder = (AdviewHolder) holder;
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
        if (position == 0 || modelList.get(position) instanceof MainViewHolder) return ITEM_TYPE_BOOK;
        else return (position % MainActivity.ADS_PER_ITEM == 0) ? ITEM_TYPE_BANNER : ITEM_TYPE_BOOK;
    }
    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public interface OnItemClickedListener {
        void onItemClicked(Model model);
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView title, subTitle;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_main_item);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }

    public static class AdviewHolder extends RecyclerView.ViewHolder {
        public AdviewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
