package com.nutrition.express.model.rest;

import android.content.Context;

import com.nutrition.express.application.Constants;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.intercept.OAuth1SigningInterceptor;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huang on 2/17/16.
 */
public class RestClient {
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    private static class Holder {
        private static RestClient holder = new RestClient();
    }

    public static RestClient getInstance() {
        return Holder.holder;
    }

    private RestClient() {
    }

    public void init(Context context) {
        Cache cache = new Cache(context.getCacheDir(), 10 * 1024 * 1024);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new OAuth1SigningInterceptor())
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public BlogService getBlogService() {
        return retrofit.create(BlogService.class);
    }

    public UserService getUserService() {
        return retrofit.create(UserService.class);
    }
}
