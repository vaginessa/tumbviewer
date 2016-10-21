package com.nutrition.express.model.rest;

import com.nutrition.express.BuildConfig;
import com.nutrition.express.model.rest.bean.BaseBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huang on 2/18/16.
 */
public class RestCallBack<T> implements Callback<BaseBean<T>> {
    private ResponseListener listener;
    private String tag;

    public RestCallBack(ResponseListener listener, String tag) {
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public void onResponse(Call<BaseBean<T>> call, Response<BaseBean<T>> response) {
        if (response.isSuccessful()) {
            // status code [200, 299]
            BaseBean<T> body = response.body();
            if (body != null) {
                listener.onResponse(body, tag);
            } else {
                listener.onFailure(tag);
            }
        } else {
            // status code [400, 599]
            // the server can return a JsonObject that contain error message.
            listener.onFailure(tag);
        }
    }

    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
        listener.onFailure(tag);
    }

}
