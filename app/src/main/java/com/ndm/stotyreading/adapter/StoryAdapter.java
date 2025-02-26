package com.ndm.stotyreading.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.activity.ActivityStoryChapter;
import com.ndm.stotyreading.enitities.story.Story;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private Context context;
    private List<Story> storyList;

    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.tvAuthor.setText("Tác giả: " + story.getAuthor());
        holder.tvStatus.setText("Trạng thái: " + story.getStatus());
        holder.tvViews.setText("Lượt đọc: " + story.getViews());
        holder.tvRating.setText("Đánh giá: " + story.getRating());

        // Load ảnh từ URL
        Glide.with(context)
                .load(story.getCoverImage())
                .into(holder.imgThumbnail);

        // Xử lý sự kiện click item
        holder.itemView.setOnClickListener(v -> {

                Intent intent = new Intent(context, ActivityStoryChapter.class);
                intent.putExtra("story_id", String.valueOf(story.getId()));

                context.startActivity(intent);

        });
    }


    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvStatus, tvViews, tvRating;
        ImageView imgThumbnail;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvRating = itemView.findViewById(R.id.tvRating);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
