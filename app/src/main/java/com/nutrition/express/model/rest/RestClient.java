package com.nutrition.express.model.rest;

import android.content.Context;
import android.util.Log;

import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.helper.AuthHeaderBuilder;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ApiService.UserService;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 2/17/16.
 */
public class RestClient {
    private static RestClient client;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

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
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder builder = originalRequest.newBuilder();
                        DataManager manager = DataManager.getInstance();
                        if (manager.isLogin()) {
                            String url = originalRequest.url().toString();
                            url = url.substring(0, url.indexOf("?"));
                            String auth = new AuthHeaderBuilder()
                                    .buildAuthHeader(originalRequest.method(),
                                            url,
                                            manager.getToken(),
                                            manager.getSecret());
                            builder.header("Authorization",
                                    "OAuth " + auth);
                        }
                        Request request = builder.build();
                        Log.d(TAG, "intercept: " + request.headers().get("Authorization"));

//                        Response response = chain.proceed(originalRequest);
//                        Logger.json(response.body().string());
                        return chain.proceed(request);
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
