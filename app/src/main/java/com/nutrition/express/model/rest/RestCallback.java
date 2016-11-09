package com.nutrition.express.model.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nutrition.express.BuildConfig;
import com.nutrition.express.model.rest.bean.BaseBean;

import java.io.IOException;

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
                BaseBean<Void> baseBean = new Gson().fromJson(response.errorBody().string(),
                        new TypeToken<BaseBean<Void>>(){}.getType());
                listener.onError(baseBean.getMeta().getStatus(), baseBean.getMeta().getMsg(), tag);
            } catch (IOException | JsonSyntaxException e) {
                listener.onFailure(e, tag);
            }
        }
    }

    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
        listener.onFailure(t, tag);
    }

}
