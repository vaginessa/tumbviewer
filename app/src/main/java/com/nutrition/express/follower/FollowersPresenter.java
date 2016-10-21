package com.nutrition.express.follower;

import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallBack;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.Users;

import retrofit2.Call;

/**
 * Created by huang on 10/18/16.
 */

public class FollowersPresenter implements FollowersContract.FollowersPresenter, ResponseListener {
    private FollowersContract.FollowersView view;
    private UserService service;
    private Call<BaseBean<Users>> call;
    private final int defaultLimit = 20;
    private int offset = 0;

    public FollowersPresenter(FollowersContract.FollowersView view) {
        this.view = view;
        service = RestClient.getInstance().getUserService();
    }

    public void getFollowers(String id) {
        if (call == null) {
            call = service.getFollowers(id, defaultLimit, offset);
            call.enqueue(new RestCallBack<Users>(this, "followers"));
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

    }

    @Override
    public void onFailure(String tag) {

    }

}
