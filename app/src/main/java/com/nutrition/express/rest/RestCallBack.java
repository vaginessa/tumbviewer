package com.nutrition.express.rest;

import com.nutrition.express.BuildConfig;
import com.nutrition.express.rest.bean.BaseBean;

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
        BaseBean<T> body = response.body();
        if (body != null) {
            listener.onResponse(body, tag);
        } else {
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
