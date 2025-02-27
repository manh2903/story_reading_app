package com.ndm.stotyreading.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.dialogs.DialogUtils;
import com.ndm.stotyreading.enitities.user.LoginRequest;
import com.ndm.stotyreading.enitities.user.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private TextInputEditText txtEmail, txtPassEdt;
    private Button btnSignIn;
    private ProgressDialog progressDialog;
    private TextView tv_forgot_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        initListener();
    }

    private void initUI() {
        layoutSignUp = findViewById(R.id.layout_sign_up);
        txtEmail = findViewById(R.id.txt_emailedt);
        txtPassEdt = findViewById(R.id.txt_passEdt);
        btnSignIn = findViewById(R.id.btn_sign_in);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        tv_forgot_pw = findViewById(R.id.tv_forgot_pass);
        tv_forgot_pw.setVisibility(View.GONE);

//        txtEmail.setText("user1");
//        txtPassEdt.setText("123");
    }

    private void initListener() {
        btnSignIn.setOnClickListener(v -> onClickSignIn());
    }

    private void onClickSignIn() {
        String strEmail = txtEmail.getText().toString().trim();
        String strPassword = txtPassEdt.getText().toString().trim();

        if (strEmail.isEmpty() || strPassword.isEmpty()) {
            DialogUtils.showErrorDialog(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu");
            return;
        }

        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(strEmail, strPassword);

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveToken(loginResponse.getToken()); // Lưu token
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    DialogUtils.showErrorDialog(LoginActivity.this, "Sai tài khoản hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                DialogUtils.showErrorDialog(LoginActivity.this, "Lỗi kết nối: " + t.getMessage());
                Log.e("LoginError", t.getMessage());
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        sharedPreferences.edit().putString("token", token).apply();
    }
}
