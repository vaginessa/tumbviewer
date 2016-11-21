package com.nutrition.express.main;

import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
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
    private boolean hasNext = true, reset = false;
    private String type;

    public DashboardPresenter(DashboardContract.View view, String type) {
        this.view = view;
        this.type = type;
        userService = RestClient.getInstance().getUserService();
    }

    private void getDashboardPosts() {
        if (call == null) {
            HashMap<String, String> options = new HashMap<>(3);
            options.put("limit", "" + defaultLimit);
            options.put("offset", "" + offset);
            options.put("type", type);
            call = userService.getDashboard(options);
            call.enqueue(new RestCallback<BlogPosts>(this, "dashboard"));
        }
    }

    @Override
    public void refresh() {
        offset = 0;
        hasNext = true;
        reset = true;
        getDashboard();
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
        if (reset) {
            reset = false;
            view.resetData(posts.getList(), hasNext);
        } else {
            view.showDashboard(posts.getList(), hasNext);
        }
    }

    @Override
    public void onError(int code, String error, String tag) {
        if (view == null) {
            return;
        }
        call = null;
        view.onError(code, error);
    }

    @Override
    public void onFailure(Throwable t, String tag) {
        if (view == null) {
            return;
        }
        call = null;
        view.onFailure(t);
    }

}
