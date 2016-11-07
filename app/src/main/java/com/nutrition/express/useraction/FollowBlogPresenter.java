package com.nutrition.express.useraction;

import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallBack;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;

import retrofit2.Call;

/**
 * Created by huang on 11/7/16.
 */

public class FollowBlogPresenter implements FollowBlogContract.Presenter, ResponseListener {
    private FollowBlogContract.View view;
    private UserService service;
    private Call<BaseBean<Void>> call;

    public FollowBlogPresenter(FollowBlogContract.View view) {
        this.view = view;
        service = RestClient.getInstance().getUserService();
    }

    public void follow(String url) {
        if (call == null) {
            call = service.follow(url);
            call.enqueue(new RestCallBack<Void>(this, "follow"));
        }
    }

    @Override
    public void unfollow(String url) {
        if (call == null) {
            call = service.unfollow(url);
            call.enqueue(new RestCallBack<Void>(this, "unfollow"));
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        view = null;
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onResponse(BaseBean baseBean, String tag) {
        if (view == null) {
            return;
        }
        call = null;
        switch (tag) {
            case "follow":
                view.onFollow();
                break;
            case "unfollow":
                view.onUnfollow();
                break;
        }
    }

    @Override
    public void onFailure(String tag) {
        if (view == null) {
            return;
        }
        call = null;
    }

}
