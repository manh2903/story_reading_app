package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class StoryResponse {
    private boolean success;
    private List<Story> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Story> getData() {
        return data;
    }
}
