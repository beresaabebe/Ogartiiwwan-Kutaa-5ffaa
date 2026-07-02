package com.beckytech.og_artiiwwankutaa5ffaa.adapter;

import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentEndPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.ContentStartPage;
import com.beckytech.og_artiiwwankutaa5ffaa.contents.TitleContents;

import java.io.File;

public class ChapterPagerAdapter extends RecyclerView.Adapter<ChapterPagerAdapter.ChapterViewHolder> {

    private final PdfRenderer pdfRenderer;

    public ChapterPagerAdapter(PdfRenderer pdfRenderer) {
        this.pdfRenderer = pdfRenderer;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_page, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.bind(position, pdfRenderer);
    }

    @Override
    public int getItemCount() {
        return TitleContents.title.length;
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.chapterRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        void bind(int position, PdfRenderer pdfRenderer) {
            int startPage = ContentStartPage.pageStart[position];
            int endPage = ContentEndPage.pageEnd[position];
            PdfPageAdapter adapter = new PdfPageAdapter(pdfRenderer, startPage, endPage);
            recyclerView.setAdapter(adapter);
        }
    }
}