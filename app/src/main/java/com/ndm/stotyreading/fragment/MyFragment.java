package com.ndm.stotyreading.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.base.BaseFragment;


public class MyFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
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
    protected void initData() {
        super.initData();
        // Khởi tạo dữ liệu nếu cần
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        
        // Thiết lập tiêu đề
        setTitle("Trang chính");
        
        // Thêm nút để test chức năng loading và WebView
        Button btnShowWebsite = view.findViewById(R.id.btn_show_website);
        Button btnToggleLoading = view.findViewById(R.id.btn_toggle_loading);
        
        if (btnShowWebsite != null) {
            btnShowWebsite.setOnClickListener(v -> {
                // Hiển thị WebView và load URL
                webView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.content_container).setVisibility(View.GONE);
                loadUrl("https://www.google.com");
            });
        }
        
        if (btnToggleLoading != null) {
            btnToggleLoading.setOnClickListener(v -> {
                if (loadingDialog.isShowing()) {
                    hideLoading();
                    showToast("Đã ẩn loading");
                } else {
                    showLoading();
                    showToast("Đã hiển thị loading");
                    
                    // Tự động ẩn sau 2 giây
                    view.postDelayed(() -> {
                        hideLoading();
                    }, 2000);
                }
            });
        }
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
        // Xử lý các sự kiện khác
    }

    @Override
    protected void observerData() {
        super.observerData();
        // Theo dõi dữ liệu nếu cần
    }
}