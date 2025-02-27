package com.ndm.stotyreading.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.activity.ActivityStoryChapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.base.BaseFragment;
import com.ndm.stotyreading.enitities.detailChapter.ChapterContentResponse;
import com.ndm.stotyreading.enitities.story.Chapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterDetailFragment extends BaseFragment {

    private static final String ARG_CHAPTER_ID = "chapter";
    private Chapter chapter;
    private WebView webView;

    public ChapterDetailFragment() {
        // Constructor rỗng bắt buộc
    }

    public static ChapterDetailFragment newInstance(Chapter chapter) {
        ChapterDetailFragment fragment = new ChapterDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHAPTER_ID, chapter);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            chapter = getArguments().getParcelable(ARG_CHAPTER_ID);
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
        return -1; // We're creating WebView dynamically, not from layout
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        setTitle("Chương " + chapter.getId() + ": " + chapter.getTitle());

//        TextView tvTitle = view.findViewById(R.id.tv_title);
//        if (tvTitle != null) {
//            tvTitle.setText("Chapter ID: " + chapter.getId());
//        }

        FrameLayout container = view.findViewById(R.id.web_view_container);
        if (container != null) {
            try {
                // Tạo WebView động
                webView = new WebView(getContext());
                webView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);

                // Thêm WebView vào container
                container.addView(webView);
            } catch (Exception e) {
                Log.e("WebViewError", "Lỗi khi khởi tạo WebView: " + e.getMessage());
                showFallbackMessage(container);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        loadChapterContent();
    }

    @Override
    protected void handleBackPress() {
        // Lấy activity chứa fragment
        ActivityStoryChapter activity = (ActivityStoryChapter) getActivity();
        if (activity != null) {
            // Ẩn fragmentContainer và hiển thị lại mainContentScrollView
            activity.findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            activity.findViewById(R.id.mainContentScrollView).setVisibility(View.VISIBLE);

            // Xóa fragment khỏi back stack
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }


    private void loadChapterContent() {
        // Kiểm tra chapterId trước khi gọi API
        if (chapter == null) {
            showToast("Không có chapterId để tải dữ liệu");
            hideLoading();
            return;
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        showLoading();

        // Sử dụng chapterId từ instance variable thay vì hardcode
        apiService.getChapterContent("Bearer " + token, chapter.getId()).enqueue(new Callback<ChapterContentResponse>() {
            @Override
            public void onResponse(Call<ChapterContentResponse> call, Response<ChapterContentResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    ChapterContentResponse contentResponse = response.body();
                    if (contentResponse.isSuccess()) {
                        String htmlContent = contentResponse.getContent();
                        Log.d("content", htmlContent);

                        if (webView != null) {
                            try {
                                String formattedHtml = htmlContent;
                                webView.loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null);
                            } catch (Exception e) {
                                Log.e("WebViewError", "Lỗi khi tải nội dung: " + e.getMessage());
                                webView.setVisibility(View.GONE);
                                showFallbackContent(htmlContent);
                            }
                        } else {
                            showFallbackContent(htmlContent);
                        }
                    } else {
                        showToast(contentResponse.getMessage() != null ?
                                contentResponse.getMessage() : "Không thể tải nội dung chương");
                    }
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
        FrameLayout container = getView().findViewById(R.id.web_view_container);
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

    @Override
    protected void handleEvent() {
        super.handleEvent();
    }

    @Override
    protected void observerData() {
        super.observerData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView = null; // Xóa tham chiếu để tránh rò rỉ bộ nhớ
    }
}