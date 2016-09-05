package com.nutrition.express.model.rest;

import android.content.Context;

import com.nutrition.express.model.rest.ApiService.BlogService;

import java.io.IOException;

import okhttp3.Cache;
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
        }
        return client;
    }

    private RestClient() {
    }

    public void init(Context context) {
        Cache cache = new Cache(context.getCacheDir(), 10 * 1024 * 1024);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .build();
//                        Response response = chain.proceed(originalRequest);
//                        Logger.json(response.body().string());
                        return chain.proceed(newRequest);
                    }
                })
                .cache(cache)
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
