package com.ndm.stotyreading.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.base.BaseFragment;
import com.ndm.stotyreading.enitities.ChapterContentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterDetailFragment extends BaseFragment {

    private static final String ARG_CHAPTER_ID = "chapter_id";
    private String chapterId;

    // Constructor mặc định
    public ChapterDetailFragment() {
        // Required empty public constructor
    }

    // Phương thức factory để tạo instance với chapterId
    public static ChapterDetailFragment newInstance(String chapterId) {
        ChapterDetailFragment fragment = new ChapterDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chapterId = getArguments().getString(ARG_CHAPTER_ID);
        }
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
        return R.id.web_view;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        // Thiết lập tiêu đề toolbar
        setTitle("Chương " + chapterId);

        // Hiển thị chapterId trong TextView nếu có
        TextView tvChapterId = view.findViewById(R.id.tvChapterId);
        if (tvChapterId != null) {
            tvChapterId.setText("Chapter ID: " + chapterId);
        }

        // Cấu hình WebView
        WebView webView = view.findViewById(getWebViewId());
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);

            // Thêm cài đặt để tự động điều chỉnh kích thước chữ
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        // Tạo API Service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Hiển thị loading
        showLoading();

        // Gọi API để lấy nội dung chương
        apiService.getChapterContent("Bearer " + token, chapterId).enqueue(new Callback<ChapterContentResponse>() {
            @Override
            public void onResponse(Call<ChapterContentResponse> call, Response<ChapterContentResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    ChapterContentResponse contentResponse = response.body();
                    if (contentResponse.isSuccess()) {
                        // Lấy nội dung HTML
                        String htmlContent = contentResponse.getContent();

                        // Tìm WebView và hiển thị nội dung
                        WebView webView = getView().findViewById(getWebViewId());
                        if (webView != null) {
                            // Tạo CSS để định dạng nội dung
                            String formattedHtml =htmlContent;

                            // Tải nội dung HTML đã định dạng
                            webView.loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null);
                        }
                    } else {
                        // Hiển thị thông báo lỗi
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

    @Override
    protected void handleEvent() {
        super.handleEvent();
        // Xử lý sự kiện đã được xử lý trong BaseFragment
    }

    @Override
    protected void observerData() {
        super.observerData();
        // Để trống vì không cần theo dõi dữ liệu khác
    }
}