package com.nutrition.express.main;

import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallBack;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogPosts;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by huang on 11/2/16.
 */

public class DashboardPresenter implements DashboardContract.Presenter, ResponseListener {
    private DashboardContract.View view;
    private UserService userService;
    private Call<BaseBean<BlogPosts>> call;
    private int defaultLimit = 20;
    private int offset = 0;
    private boolean hasNext = true;
    private String type;

    public DashboardPresenter(DashboardContract.View view, String type) {
        this.view = view;
        this.type = type;
        userService = RestClient.getInstance().getUserService();
    }

    private void getDashboardPosts() {
        if (call == null) {
            HashMap<String, String> options = new HashMap<>(2);
            options.put("limit", "" + defaultLimit);
            options.put("offset", "" + offset);
            options.put("type", type);
            call = userService.getDashboard("OAuth", options);
            call.enqueue(new RestCallBack<BlogPosts>(this, "dashboard"));
        }
    }

    @Override
    public void getDashboard() {
        getDashboardPosts();
    }

    @Override
    public void getNextDashboard() {
        getDashboardPosts();
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
        if (view == null) {
            return;
        }
        call = null;
        BlogPosts posts = (BlogPosts) baseBean.getResponse();
        offset += posts.getList().size();
        if (posts.getList().size() < defaultLimit) {
            hasNext = false;
        }
        view.showDashboard(posts.getList(), hasNext);
    }

    @Override
    public void onFailure(String tag) {
        if (view == null) {
            return;
        }
        call = null;
        if (offset > 0) {
            view.showLoadingNextFailure();
        } else {
            view.showLoadingFailure();
        }
    }

}
