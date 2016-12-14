package com.nutrition.express.main;

import android.text.TextUtils;

import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogPosts;
import com.nutrition.express.model.rest.bean.PostsItem;
import com.nutrition.express.model.rest.bean.TrailItem;

import java.util.HashMap;
import java.util.List;

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
    private long lastTimestamp = System.currentTimeMillis();
    private boolean hasNext = true, reset = false;
    private String type;
    private DataManager dataManager = DataManager.getInstance();

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
        List<PostsItem> postsItems = ((BlogPosts) baseBean.getResponse()).getList();
        offset += postsItems.size();
        if (postsItems.size() < defaultLimit) {
            hasNext = false;
        }
        if (reset) {
            reset = false;
            view.resetData(postsItems, hasNext);
        } else {
            postsItems = removeDuplicate(postsItems);
            if (postsItems.size() > 0) {
                view.showDashboard(postsItems, hasNext);
            } else {
                getNextDashboard();
                return;
            }
        }
        lastTimestamp = postsItems.get(postsItems.size() - 1).getTimestamp();
        for (PostsItem item : postsItems) {
            dataManager.addFollowingBlog(item.getBlog_name());
            if (!TextUtils.isEmpty(item.getSource_title())) {
                dataManager.addReferenceBlog(item.getSource_title());
            }
            for (TrailItem trailItem : item.getTrail()) {
                if (!TextUtils.isEmpty(trailItem.getBlog().getName())) {
                    dataManager.addReferenceBlog(trailItem.getBlog().getName());
                }
            }
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

    private List<PostsItem> removeDuplicate(List<PostsItem> postsItems) {
        int i = 0;
        for (; i < postsItems.size(); i++) {
            if (lastTimestamp > postsItems.get(i).getTimestamp()) {
                break;
            }
        }
        if (i > 0 && i < postsItems.size()) {
            postsItems = postsItems.subList(i, postsItems.size());
        }
        return postsItems;
    }

}
