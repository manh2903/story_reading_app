package com.ndm.stotyreading.api;


import com.ndm.stotyreading.enitities.ChapterContentResponse;
import com.ndm.stotyreading.enitities.story.StoryChapterRespone;
import com.ndm.stotyreading.enitities.story.StoryResponse;
import com.ndm.stotyreading.enitities.user.LoginRequest;
import com.ndm.stotyreading.enitities.user.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("common/stories")
    Call<StoryResponse> getStories(
            @Header("Authorization") String token
    );

    @GET("common/story/{storyId}/chapters")
    Call<StoryChapterRespone> getStoryChapters(
            @Header("Authorization") String token,
            @Path("storyId") String storyId
    );

    @GET("common/chapter/{chapterId}")
    Call<ChapterContentResponse> getChapterContent(
            @Header("Authorization") String token,
            @Path("chapterId") String chapterId
    );
}
