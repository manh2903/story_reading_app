package com.ndm.stotyreading.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.ndm.stotyreading.R;

public abstract class BaseFragment extends Fragment {

    protected TextView tvTitle;
    protected ImageButton btnBack;
    protected Dialog loadingDialog;
    protected WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        createLoadingDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        // Tìm các view cơ bản
        tvTitle = view.findViewById(getToolbarTitleId());
        btnBack = view.findViewById(getBackButtonId());
        webView = view.findViewById(getWebViewId());

        // Thiết lập nút back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> handleBackPress());
        }

        // Thiết lập WebView nếu có
        setupWebView();

        // Xử lý nút back của hệ thống
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        handleBackPress();
                    }
                });

        observerData();
        initView(view);
        handleEvent();

        return view;
    }

    /**
     * Xử lý sự kiện khi nút back được nhấn
     */
    protected void handleBackPress() {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination().getId() == navController.getGraph().getStartDestinationId()) {
            requireActivity().finishAndRemoveTask();
        } else {
            navController.navigateUp();
        }
    }

    /**
     * Thiết lập WebView với các cài đặt cơ bản
     */
    protected void setupWebView() {
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    hideLoading();
                }
            });
        }
    }

    /**
     * Tạo dialog loading
     */
    private void createLoadingDialog() {
        loadingDialog = new Dialog(requireContext());
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(getLoadingLayoutId());
        loadingDialog.setCancelable(false);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * Hiển thị dialog loading
     */
    protected void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing() && isAdded()) {
            loadingDialog.show();
        }
    }

    /**
     * Ẩn dialog loading
     */
    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Tải URL trong WebView
     *
     * @param url URL cần tải
     */
    protected void loadUrl(String url) {
        if (webView != null) {
            showLoading();
            webView.loadUrl(url);
        }
    }

    /**
     * Hiển thị thông báo toast
     *
     * @param message Nội dung thông báo
     */
    protected void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Thiết lập tiêu đề cho toolbar
     *
     * @param title Tiêu đề cần hiển thị
     */
    protected void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    // Các phương thức trừu tượng bắt buộc phải triển khai

    /**
     * @return ID layout của fragment
     */
    protected abstract int getLayoutId();

    /**
     * @return ID của TextView hiển thị tiêu đề, nếu không có trả về -1
     */
    protected int getToolbarTitleId() {
        return -1;
    }

    /**
     * @return ID của ImageButton nút back, nếu không có trả về -1
     */
    protected int getBackButtonId() {
        return -1;
    }

    /**
     * @return ID của WebView, nếu không có trả về -1
     */
    protected int getWebViewId() {
        return -1;
    }

    /**
     * @return ID layout của dialog loading
     */
    protected int getLoadingLayoutId() {
        return R.layout.dialog_loading; // Giả sử có file layout dialog_loading.xml
    }

    // Các phương thức có thể ghi đè

    protected void initData() {
        // Triển khai mặc định trống
    }

    protected void initView(View view) {
        // Triển khai mặc định trống
    }

    protected void handleEvent() {
        // Triển khai mặc định trống
    }

    protected void observerData() {
        // Triển khai mặc định trống
    }
}