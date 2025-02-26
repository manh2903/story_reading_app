package com.ndm.stotyreading.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ndm.stotyreading.utils.Constants;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Interceptor detailedLoggingInterceptor = chain -> {
                Request request = chain.request();

                System.out.println("========== REQUEST ==========");
                System.out.println("URL: " + request.url());
                System.out.println("Method: " + request.method());
                System.out.println("Headers: " + request.headers());
                if (request.body() != null) {
                    System.out.println("Body: " + request.body().toString());
                }


                Response response = chain.proceed(request);

                System.out.println("========== RESPONSE ==========");
                System.out.println("Status Code: " + response.code());
                System.out.println("Headers: " + response.headers());
                if (response.body() != null) {
                    System.out.println("Body: " + response.peekBody(Long.MAX_VALUE).string());
                }

                return response;
            };

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(detailedLoggingInterceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Khởi tạo Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
