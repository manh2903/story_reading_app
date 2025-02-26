package com.ndm.stotyreading.enitities.story;

public class ChapterImage {
    private int id;
    private String chapter_id;
    private String image_url;
    private int order;
    private String description;

    public int getId() {
        return id;
    }

    public String getChapterId() {
        return chapter_id;
    }

    public String getImageUrl() {
        return image_url;
    }

    public int getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }
}
