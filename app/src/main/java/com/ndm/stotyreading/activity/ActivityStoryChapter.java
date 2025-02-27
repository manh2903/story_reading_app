package com.ndm.stotyreading.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
;

import com.bumptech.glide.Glide;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.ChapterAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Chapter;
import com.ndm.stotyreading.enitities.story.StoryChapterRespone;
import com.ndm.stotyreading.fragment.ChapterDetailFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityStoryChapter extends AppCompatActivity {

    private static final String ARG_STORY_ID = "story_id";
    private String storyId;
    private TextView tvTitle, tvAuthor, tvStoryId, tvGenreId, tvDescription, tvStatus, tvViews, tvRating;
    private ImageView ivCoverImage;
    private RecyclerView rvChapters;
    private ChapterAdapter chapterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_chapter);
        storyId = getIntent().getStringExtra(ARG_STORY_ID);


        // Ánh xạ View
        ivCoverImage = findViewById(R.id.ivCoverImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvStoryId = findViewById(R.id.tvStoryId);
        tvGenreId = findViewById(R.id.tvGenreId);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvViews = findViewById(R.id.tvViews);
        tvRating = findViewById(R.id.tvRating);
        rvChapters = findViewById(R.id.rvChapters);

        // Setup RecyclerView
        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API lấy thông tin truyện
        loadStoryDetails();
//        isWebViewAvailable();
        
    }
    private void loadStoryDetails() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getStoryChapters("Bearer " + token, storyId).enqueue(new Callback<StoryChapterRespone>() {
            @Override
            public void onResponse(@NonNull Call<StoryChapterRespone> call, @NonNull Response<StoryChapterRespone> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StoryChapterRespone storyChapterRespone = response.body();

                    if (storyChapterRespone.isSuccess()) {
                        // Hiển thị thông tin truyện
                        tvStoryId.setText("ID: " + storyChapterRespone.getStory().getId());
                        tvTitle.setText(storyChapterRespone.getStory().getTitle());
                        tvAuthor.setText("Tác giả: " + storyChapterRespone.getStory().getAuthor());
                        tvGenreId.setText("Thể loại ID: " + storyChapterRespone.getStory().getGenreId());
                        tvDescription.setText("Mô tả: " + storyChapterRespone.getStory().getDescription());
                        tvStatus.setText("Trạng thái: " + storyChapterRespone.getStory().getStatus());
                        tvViews.setText("Lượt xem: " + storyChapterRespone.getStory().getViews());
                        tvRating.setText("Đánh giá: " + storyChapterRespone.getStory().getRating());

                        // Tải ảnh bìa bằng Glide

                        Glide.with(ActivityStoryChapter.this)
                                .load(storyChapterRespone.getStory().getCoverImage())
                                .into(ivCoverImage);

                        // Hiển thị danh sách chương
                        List<Chapter> chapters = storyChapterRespone.getChapters();
                        chapterAdapter = new ChapterAdapter(chapters, ActivityStoryChapter.this::onChapterClick); // Pass the listener
                        rvChapters.setAdapter(chapterAdapter);
                    } else {
                        Toast.makeText(ActivityStoryChapter.this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityStoryChapter.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoryChapterRespone> call, @NonNull Throwable t) {
                Toast.makeText(ActivityStoryChapter.this, "Lỗi khi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Lỗi khi tải dữ liệu:",  t.getMessage());

            }
        });
    }

    public void onChapterClick(Chapter chapter) {
        // Show the fragment container and hide the main content
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.mainContentScrollView).setVisibility(View.GONE);

        // Replace the FrameLayout with ChapterDetailFragment
        Fragment chapterDetailFragment = ChapterDetailFragment.newInstance(chapter);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, chapterDetailFragment)
                .addToBackStack(null) // Add to back stack to allow back navigation
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.fragmentContainer).getVisibility() == View.VISIBLE) {
            // If the fragment container is visible, hide it and show the main content
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            findViewById(R.id.mainContentScrollView).setVisibility(View.VISIBLE);
            super.onBackPressed(); // This will pop the fragment from the back stack
        } else {
            // Normal back button behavior
            super.onBackPressed();
        }
    }

}