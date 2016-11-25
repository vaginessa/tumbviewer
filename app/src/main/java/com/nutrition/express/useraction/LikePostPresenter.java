package com.nutrition.express.useraction;

import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;

import retrofit2.Call;

/**
 * Created by huang on 11/7/16.
 */

public class LikePostPresenter implements LikePostContract.Presenter, ResponseListener {
    private LikePostContract.View view;
    private UserService userService;
    private Call<BaseBean<Void[]>> call;
    private long id;

    public LikePostPresenter(LikePostContract.View view) {
        this.view = view;
        userService = RestClient.getInstance().getUserService();
    }

    @Override
    public void like(long id, String reblogKey) {
        if (call == null) {
            this.id = id;
            call = userService.like(id, reblogKey);
            call.enqueue(new RestCallback<Void[]>(this, "like"));
        }
    }

    @Override
    public void unlike(long id, String reblogKey) {
        if (call == null) {
            this.id = id;
            call = userService.unlike(id, reblogKey);
            call.enqueue(new RestCallback<Void[]>(this, "unlike"));
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
        if (null == view) {
            return;
        }
        call = null;
        switch (tag) {
            case "like":
                view.onLike(id);
                break;
            case "unlike":
                view.onUnlike(id);
                break;
        }
    }

    @Override
    public void onError(int code, String error, String tag) {
       onFailure(tag);
    }

    @Override
    public void onFailure(Throwable t, String tag) {
        onFailure(tag);
    }

    private void onFailure(String tag) {
        if (null == view) {
            return;
        }
        call = null;
        switch (tag) {
            case "like":
                view.onLikeFailure();
                break;
            case "unlike":
                view.onUnlikeFailure();
                break;
        }
    }

}
