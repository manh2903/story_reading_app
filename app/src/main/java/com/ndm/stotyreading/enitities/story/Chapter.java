package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class Chapter {
    private String id;
    private String story_id;
    private int chapter_number;
    private String title;
    private String release_date;
    private int views;
    private String created_at;
    private List<ChapterImage> chapterImages;

    public String getId() {
        return id;
    }

    public String getStoryId() {
        return story_id;
    }

    public int getChapterNumber() {
        return chapter_number;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public int getViews() {
        return views;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public List<ChapterImage> getChapterImages() {
        return chapterImages;
    }
}
