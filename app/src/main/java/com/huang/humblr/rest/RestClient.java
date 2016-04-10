package com.huang.humblr.rest;

import android.content.Context;

import com.huang.humblr.rest.ApiService.BlogService;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huang on 2/17/16.
 */
public class RestClient {
    private static RestClient client;
    private Retrofit retrofit;

    public static RestClient getInstance() {
        if (client == null) {
            client = new RestClient();
            client.init(null);
        }
        return client;
    }

    private RestClient() {
    }

    public void init(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .build();
                        Response response = chain.proceed(originalRequest);
                        Logger.json(response.body().string());
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.tumblr.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public BlogService getBlogService() {
        return retrofit.create(BlogService.class);
    }
}
