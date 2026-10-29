package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import java.util.ArrayList;
import java.util.List;

public class PdfPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PDF_PAGE = 0;
    private static final int TYPE_AD_BANNER = 1;

    private final List<Object> items = new ArrayList<>();
    private final PdfRenderer pdfRenderer;

    // Enterprise LRU Bitmap Cache (20MB limit for smooth scrolling & no OOM)
    private static final LruCache<Integer, Bitmap> bitmapCache = new LruCache<Integer, Bitmap>(20 * 1024 * 1024) {
        @Override
        protected int sizeOf(Integer key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };

    public PdfPageAdapter(PdfRenderer pdfRenderer, int startPage, int endPage) {
        this.pdfRenderer = pdfRenderer;
        int pageCount = 0;
        for (int i = startPage; i <= endPage; i++) {
            items.add(i);
            pageCount++;
            if (pageCount % 4 == 0) {
                items.add("BANNER");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Integer) return TYPE_PDF_PAGE;
        return TYPE_AD_BANNER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PDF_PAGE) {
            return new PdfPageViewHolder(inflater.inflate(R.layout.item_pdf_page, parent, false));
        } else {
            return new BannerAdViewHolder(inflater.inflate(R.layout.item_ad_container, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PdfPageViewHolder) {
            ((PdfPageViewHolder) holder).bind((Integer) items.get(position), pdfRenderer);
        } else if (holder instanceof BannerAdViewHolder) {
            ((BannerAdViewHolder) holder).loadBanner();
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class PdfPageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PdfPageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfPageView);
        }
        void bind(int pageIndex, PdfRenderer renderer) {
            Bitmap cached = bitmapCache.get(pageIndex);
            if (cached != null && !cached.isRecycled()) {
                imageView.setImageBitmap(cached);
                return;
            }
            try {
                PdfRenderer.Page page = renderer.openPage(pageIndex);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                bitmapCache.put(pageIndex, bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    static class BannerAdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;
        BannerAdViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ad_container);
        }
        void loadBanner() {
            container.removeAllViews();
            AdView adView = new AdView(itemView.getContext(), itemView.getContext().getString(R.string.fb_banner_ads_detail), AdSize.BANNER_HEIGHT_50);
            container.addView(adView);
            adView.loadAd();
        }
    }
}
