package com.nutrition.express.model.rest;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nutrition.express.BuildConfig;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.main.MainActivity;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.ErrorBean;

import java.io.IOException;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huang on 2/18/16.
 */
public class RestCallback<T> implements Callback<BaseBean<T>> {
    private ResponseListener listener;
    private String tag;

    public RestCallback(ResponseListener listener, String tag) {
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public void onResponse(Call<BaseBean<T>> call, Response<BaseBean<T>> response) {
        if (response.isSuccessful()) {
            // status code [200, 299]
            BaseBean<T> body = response.body();
            listener.onResponse(body, tag);
        } else {
            // status code [400, 599]
            // the server return a JsonObject that contain error message.
            try {
                ErrorBean errorBean = new Gson().fromJson(response.errorBody().string(),
                        new TypeToken<ErrorBean>(){}.getType());
                handleError(errorBean);
            } catch (IOException | JsonSyntaxException e) {
                listener.onFailure(e, tag);
            }
        }
        Headers headers = response.headers();
        DataManager.getInstance().updateTumblrAppInfo(
                headers.get("X-RateLimit-PerDay-Limit"),
                headers.get("X-RateLimit-PerDay-Remaining"),
                headers.get("X-RateLimit-PerDay-Reset"),
                headers.get("X-RateLimit-PerHour-Limit"),
                headers.get("X-RateLimit-PerHour-Remaining"),
                headers.get("X-RateLimit-PerHour-Reset"));
    }

    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
//        if (t.getMessage().equals("Canceled")) {
        if (TextUtils.equals(t.getMessage(), "Canceled")) {
            listener.onError(0, "Failed, touch to retry", tag);
        } else {
            listener.onFailure(t, tag);
        }
    }

    private void handleError(ErrorBean errorBean) {
        synchronized (RestCallback.class) {
            if (errorBean.getMeta().getStatus() == 401) {
                RestClient.getInstance().cancelAllCall();
                //Unauthorized, need login again
                DataManager dataManager = DataManager.getInstance();
                dataManager.removeAccount(dataManager.getPositiveAccount());
                if (dataManager.switchToNextRoute()) {
                    listener.onError(401, "Failed, touch to retry", tag);
                } else {
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager
                            .getInstance(ExpressApplication.getApplication());
                    Intent intent = new Intent(MainActivity.ERROR_401);
                    broadcastManager.sendBroadcast(intent);
                }
            } else if (errorBean.getMeta().getStatus() == 429) {
                RestClient.getInstance().cancelAllCall();
                //429 request limit exceeded
                DataManager dataManager = DataManager.getInstance();
                if (dataManager.switchToNextRoute()) {
                    listener.onError(429, "Failed, touch to retry", tag);
                } else {
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager
                            .getInstance(ExpressApplication.getApplication());
                    Intent intent = new Intent(MainActivity.ERROR_429);
                    broadcastManager.sendBroadcast(intent);
                }
            } else {
                listener.onError(errorBean.getMeta().getStatus(), errorBean.getMeta().getMsg(), tag);
            }
        }
    }

}
