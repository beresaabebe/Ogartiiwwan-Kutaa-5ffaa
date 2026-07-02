package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.util.ArrayList;
import java.util.List;

public class PdfPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PDF_PAGE = 0;
    private static final int TYPE_AD_BANNER = 1;
    private static final int TYPE_AD_NATIVE = 2;

    private final List<Object> items = new ArrayList<>();
    private final PdfRenderer pdfRenderer;

    public PdfPageAdapter(PdfRenderer pdfRenderer, int startPage, int endPage) {
        this.pdfRenderer = pdfRenderer;
        int pageCount = 0;
        for (int i = startPage; i <= endPage; i++) {
            items.add(i);
            pageCount++;
            if (pageCount % 4 == 0) {
                if ((pageCount / 4) % 2 == 0) items.add("NATIVE");
                else items.add("BANNER");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Integer) return TYPE_PDF_PAGE;
        if ("NATIVE".equals(item)) return TYPE_AD_NATIVE;
        return TYPE_AD_BANNER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PDF_PAGE) {
            return new PdfPageViewHolder(inflater.inflate(R.layout.item_pdf_page, parent, false));
        } else if (viewType == TYPE_AD_NATIVE) {
            return new NativeAdViewHolder(inflater.inflate(R.layout.item_ad_container, parent, false));
        } else {
            return new BannerAdViewHolder(inflater.inflate(R.layout.item_ad_container, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PdfPageViewHolder) {
            ((PdfPageViewHolder) holder).bind((Integer) items.get(position), pdfRenderer);
        } else if (holder instanceof NativeAdViewHolder) {
            ((NativeAdViewHolder) holder).loadNativeAd();
        } else if (holder instanceof BannerAdViewHolder) {
            ((BannerAdViewHolder) holder).loadBanner(position / 5);
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
            try {
                PdfRenderer.Page page = renderer.openPage(pageIndex);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                imageView.setImageBitmap(bitmap);
                page.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    static class BannerAdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;
        BannerAdViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ad_container);
        }
        void loadBanner(int adIndex) {
            container.removeAllViews();
            AdView adView = new AdView(itemView.getContext());
            adView.setAdSize(adIndex % 2 == 0 ? AdSize.MEDIUM_RECTANGLE : AdSize.BANNER);
            adView.setAdUnitId(itemView.getContext().getString(R.string.google_banner_ads_unit_id));
            container.addView(adView);
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;
        NativeAdViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.ad_container);
        }
        void loadNativeAd() {
            AdLoader adLoader = new AdLoader.Builder(itemView.getContext(), itemView.getContext().getString(R.string.google_native_ads_unit_id))
                    .forNativeAd(nativeAd -> {
                        NativeAdView adView = (NativeAdView) LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_native_ad, null);
                        populateNativeAdView(nativeAd, adView);
                        container.removeAllViews();
                        container.addView(adView);
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Fallback to banner if native fails
                            AdView banner = new AdView(itemView.getContext());
                            banner.setAdSize(AdSize.BANNER);
                            banner.setAdUnitId(itemView.getContext().getString(R.string.google_banner_ads_unit_id));
                            container.removeAllViews();
                            container.addView(banner);
                            banner.loadAd(new AdRequest.Builder().build());
                        }
                    }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

        private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            if (nativeAd.getBody() == null) adView.getBodyView().setVisibility(View.INVISIBLE);
            else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
            if (nativeAd.getCallToAction() == null) adView.getCallToActionView().setVisibility(View.INVISIBLE);
            else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
            if (nativeAd.getIcon() == null) adView.getIconView().setVisibility(View.GONE);
            else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
            adView.setNativeAd(nativeAd);
        }
    }
}