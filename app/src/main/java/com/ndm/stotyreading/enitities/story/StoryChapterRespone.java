package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class StoryChapterRespone {
    private boolean success;
    private Story story;
    private List<Chapter> chapters;

    public Story getStory() {
        return story;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }


}
