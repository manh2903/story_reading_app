package com.ndm.stotyreading.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.user.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 2000; // 2 giây

    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkAuthentication();
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
    }

    private void navigateToNextScreen() {
        Class<?> targetActivity = isAuthenticated ? MainActivity.class : LoginActivity.class;
        startActivity(new Intent(this, targetActivity));
        finish();
    }

    private void checkAuthentication() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null || token.isEmpty()) {
            isAuthenticated = false;
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse.User> call = apiService.auth("Bearer " + token);

        call.enqueue(new Callback<LoginResponse.User>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse.User> call, @NonNull Response<LoginResponse.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isAuthenticated = true;
                    Log.d(TAG, "Authentication successful");
                } else {
                    handleAuthenticationFailure(prefs, "Phiên đăng nhập hết hạn");
                    Log.w(TAG, "Authentication failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse.User> call, @NonNull Throwable t) {
                handleAuthenticationFailure(prefs, "Không thể kết nối đến server");
                Log.e(TAG, "API Error: " + t.getMessage());
            }
        });
    }

    private void handleAuthenticationFailure(SharedPreferences prefs, String message) {
        isAuthenticated = false;
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
        prefs.edit().remove("token").apply();
    }
}
