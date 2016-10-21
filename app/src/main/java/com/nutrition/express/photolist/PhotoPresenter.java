package com.nutrition.express.photolist;


import com.nutrition.express.application.Constants;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallBack;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogPosts;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by huang on 10/19/16.
 */

public class PhotoPresenter implements PhotoContract.PhotoPresenter, ResponseListener {
    private PhotoContract.PhotoView view;
    private BlogService service;
    private Call<BaseBean<BlogPosts>> call;

    public PhotoPresenter(PhotoContract.PhotoView view) {
        this.view = view;
        service = RestClient.getInstance().getBlogService();
    }

    @Override
    public void getPhotos(String id) {
        if (call == null) {
            HashMap<String, String> hashMap = new HashMap<>();
            call = service.getBlogPosts(id, "photo", Constants.CONSUMER_KEY, hashMap);
            call.enqueue(new RestCallBack<BlogPosts>(this, "photo"));
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        view = null;
    }

    @Override
    public void onResponse(BaseBean baseBean, String tag) {
        call = null;
        if (view != null) {
            view.showPhotos(((BlogPosts) baseBean.getResponse()));
        }

    }

    @Override
    public void onFailure(String tag) {
        call = null;
    }

}
