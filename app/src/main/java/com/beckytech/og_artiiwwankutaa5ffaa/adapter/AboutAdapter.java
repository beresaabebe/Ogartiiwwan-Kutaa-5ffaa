package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.model.AboutModel;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.AboutViewHolder> {
    private final List<AboutModel> modelList;
    private final OnLinkClicked linkClicked;
    private long lastClickTime = 0; // Prevent double-taps at the adapter level

    public AboutAdapter(List<AboutModel> modelList, OnLinkClicked linkClicked) {
        this.modelList = modelList;
        this.linkClicked = linkClicked;
    }

    public interface OnLinkClicked {
        void linkClicked(AboutModel model);
    }

    @NonNull
    @Override
    public AboutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Professional approach: Inflate using the parent's context directly
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_list_item, parent, false);
        return new AboutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutViewHolder holder, int position) {
        AboutModel model = modelList.get(position);

        holder.name.setText(model.getName());
        holder.imageView.setImageResource(model.getImage());

        // Anti-Double Click Logic for high revenue stability
        holder.itemView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > 1000) { // 1 second debounce
                lastClickTime = currentTime;
                if (linkClicked != null) {
                    linkClicked.linkClicked(model);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList != null ? modelList.size() : 0;
    }

    // Static inner class is correct (avoids memory leaks)
    public static class AboutViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView name;

        public AboutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_about);
            name = itemView.findViewById(R.id.about_name);
        }
    }
}