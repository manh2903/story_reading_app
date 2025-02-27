package com.ndm.stotyreading.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.enitities.story.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<Chapter> chapterList;
    private OnChapterClickListener onChapterClickListener;

    // Define the listener interface
    public interface OnChapterClickListener {
        void onChapterClick(String chapterId, List<Chapter> chapterList);
    }


    public ChapterAdapter(List<Chapter> chapterList, OnChapterClickListener listener) {
        this.chapterList = chapterList;
        this.onChapterClickListener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvChapterNumber.setText("Chương " + chapter.getChapterNumber());
        holder.tvChapterTitle.setText(chapter.getTitle());
        holder.tvReleaseDate.setText(chapter.getReleaseDate());

        // Xử lý sự kiện click vào chương (nếu cần)
        holder.itemView.setOnClickListener(v -> {
            if (onChapterClickListener != null) {
                onChapterClickListener.onChapterClick(chapter.getId(), chapterList); // Assuming Chapter has getId()
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList != null ? chapterList.size() : 0;
    }

    // ViewHolder class
    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterNumber, tvChapterTitle, tvReleaseDate;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterNumber = itemView.findViewById(R.id.tvChapterNumber);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
        }
    }
}