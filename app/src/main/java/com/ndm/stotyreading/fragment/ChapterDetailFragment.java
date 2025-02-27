package com.ndm.stotyreading.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.activity.ActivityStoryChapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.base.BaseFragment;
import com.ndm.stotyreading.enitities.detailChapter.ChapterContentResponse;
import com.ndm.stotyreading.enitities.story.Chapter;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterDetailFragment extends BaseFragment {

    private static final String ARG_CHAPTER_ID = "chapter";
    private static final String TAG = "ChapterDetailFragment";

    // Các biến liên quan đến dữ liệu chương
    private String chapterId;
    private List<Chapter> chapterList;
    private int currentChapterNumber;
    private String currentChapterTitle;
    private int totalChapters;

    // Các view
    private WebView webView;
    private Button btnPreviousChapter;
    private Button btnNextChapter;
    private TextView tvChapterCounter;

    // Factory method để tạo instance mới
    public static ChapterDetailFragment newInstance(String chapterId, List<Chapter> chapters) {
        ChapterDetailFragment fragment = new ChapterDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_ID, chapterId);
        args.putSerializable("chapter_list", (Serializable) chapters);
        fragment.setArguments(args);
        return fragment;
    }

    //region Lifecycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            chapterId = getArguments().getString(ARG_CHAPTER_ID);
            chapterList = (List<Chapter>) getArguments().getSerializable("chapter_list");
            totalChapters = chapterList != null ? chapterList.size() : 0;
            updateCurrentChapterInfo(chapterId);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter_detail;
    }

    @Override
    protected int getToolbarTitleId() {
        return R.id.tv_title;
    }

    @Override
    protected int getBackButtonId() {
        return R.id.btn_back;
    }

    @Override
    protected int getWebViewId() {
        return -1; // WebView được tạo động
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        setupToolbar(view);
        setupWebView(view);
        setupNavigationButtons(view);
        updateChapterDisplay();
    }

    @Override
    protected void initData() {
        super.initData();
        loadChapterContent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView = null; // Ngăn rò rỉ bộ nhớ
    }

    //endregion

    //region Event Handling

    @Override
    protected void handleEvent() {
        super.handleEvent();
        btnPreviousChapter.setOnClickListener(v -> navigateToPreviousChapter());
        btnNextChapter.setOnClickListener(v -> navigateToNextChapter());
    }

    @Override
    protected void handleBackPress() {
        ActivityStoryChapter activity = (ActivityStoryChapter) getActivity();
        if (activity != null) {
            activity.findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            activity.findViewById(R.id.mainContentScrollView).setVisibility(View.VISIBLE);
            popBackStackIfNeeded();
        }
    }

    //endregion

    //region Helper Methods

    private void setupToolbar(View view) {
        setTitle("Chương " + currentChapterNumber + ": " + currentChapterTitle);
    }

    private void setupWebView(View view) {
        FrameLayout container = view.findViewById(R.id.web_view_container);
        if (container == null) return;

        try {
            webView = new WebView(getContext());
            webView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            webView.setVisibility(View.VISIBLE);
            container.addView(webView);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing WebView: " + e.getMessage());
            showFallbackMessage(container);
        }
    }

    private void setupNavigationButtons(View view) {
        btnPreviousChapter = view.findViewById(R.id.btn_previous_chapter);
        btnNextChapter = view.findViewById(R.id.btn_next_chapter);
        tvChapterCounter = view.findViewById(R.id.tv_chapter_counter);
    }

    private void loadChapterContent() {
        if (chapterId == null) {
            showToast("Không có chapterId để tải dữ liệu");
            hideLoading();
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        showLoading();
        apiService.getChapterContent("Bearer " + token, chapterId).enqueue(new Callback<ChapterContentResponse>() {
            @Override
            public void onResponse(Call<ChapterContentResponse> call, Response<ChapterContentResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulResponse(response.body());
                } else {
                    showToast("Không thể tải nội dung chương");
                }
            }

            @Override
            public void onFailure(Call<ChapterContentResponse> call, Throwable t) {
                hideLoading();
                showToast("Lỗi: " + t.getMessage());
            }
        });
    }

    private void handleSuccessfulResponse(ChapterContentResponse contentResponse) {
        if (contentResponse.isSuccess()) {
            String htmlContent = contentResponse.getContent();
            Log.d(TAG, "Content loaded: " + htmlContent);
            if (webView != null) {
                try {
                    webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading content into WebView: " + e.getMessage());
                    webView.setVisibility(View.GONE);
                    showFallbackContent(htmlContent);
                }
            } else {
                showFallbackContent(htmlContent);
            }
        } else {
            showToast(contentResponse.getMessage() != null ? contentResponse.getMessage() : "Không thể tải nội dung chương");
        }
    }

    private void showFallbackMessage(ViewGroup container) {
        TextView fallbackText = new TextView(getContext());
        fallbackText.setText("Không thể hiển thị nội dung do WebView không khả dụng. Vui lòng cập nhật hoặc cài đặt WebView từ Play Store.");
        fallbackText.setTextSize(16);
        fallbackText.setPadding(16, 16, 16, 16);
        container.addView(fallbackText);
        showToast("Vui lòng cập nhật hoặc cài đặt WebView từ Play Store.");
        promptInstallWebView();
    }

    private void showFallbackContent(String htmlContent) {
        FrameLayout container = requireView().findViewById(R.id.web_view_container);
        if (container != null) {
            TextView contentTextView = new TextView(getContext());
            contentTextView.setText(android.text.Html.fromHtml(htmlContent, android.text.Html.FROM_HTML_MODE_COMPACT));
            contentTextView.setTextSize(16);
            contentTextView.setPadding(16, 16, 16, 16);
            container.addView(contentTextView);
        }
    }

    private void promptInstallWebView() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.google.android.webview"));
            startActivity(intent);
        } catch (Exception e) {
            showToast("Không thể mở Play Store. Vui lòng cài đặt Android System WebView thủ công.");
        }
    }

    private void updateCurrentChapterInfo(String chapterId) {
        if (chapterList == null) return;
        for (Chapter chapter : chapterList) {
            if (chapter.getId().equals(chapterId)) {
                currentChapterNumber = chapter.getChapterNumber();
                currentChapterTitle = chapter.getTitle();
                return;
            }
        }
        currentChapterNumber = 1; // Giá trị mặc định
        currentChapterTitle = "";
    }

    private int findIndexByChapterNumber(int chapterNumber) {
        if (chapterList == null) return -1;
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getChapterNumber() == chapterNumber) {
                return i;
            }
        }
        return -1;
    }

    private void updateChapterDisplay() {
        tvChapterCounter.setText("Chương " + currentChapterNumber + "/" + totalChapters);
        setTitle("Chương " + currentChapterNumber + ": " + currentChapterTitle);
    }

    private void navigateToPreviousChapter() {
        int previousIndex = findIndexByChapterNumber(currentChapterNumber - 1);
        if (previousIndex >= 0) {
            chapterId = chapterList.get(previousIndex).getId();
            updateCurrentChapterInfo(chapterId);
            loadChapterContent();
            updateChapterDisplay();
        } else {
            showToast("Đây là chương đầu tiên!");
        }
    }

    private void navigateToNextChapter() {
        int nextIndex = findIndexByChapterNumber(currentChapterNumber + 1);
        if (nextIndex >= 0 && nextIndex < totalChapters) {
            chapterId = chapterList.get(nextIndex).getId();
            updateCurrentChapterInfo(chapterId);
            loadChapterContent();
            updateChapterDisplay();
        } else {
            showToast("Đây là chương cuối cùng!");
        }
    }

    private void popBackStackIfNeeded() {
        if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    //endregion
}