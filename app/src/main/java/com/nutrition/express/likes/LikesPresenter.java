package com.nutrition.express.likes;

import android.text.TextUtils;

import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogLikes;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by huang on 11/8/16.
 */

public class LikesPresenter implements LikesContract.LikesPresenter, ResponseListener {
    private LikesContract.LikesView view;
    private UserService userService;
    private BlogService blogService;
    private Call<BaseBean<BlogLikes>> call;
    private final int limit = 20;
    private int offset = 0;
    private String name;

    public LikesPresenter(LikesContract.LikesView view) {
        this.view = view;
        userService = RestClient.getInstance().getUserService();
    }

    @Override
    public void getLikePosts() {
        if (call == null) {
            if (userService == null) {
                userService = RestClient.getInstance().getUserService();
            }
            call = userService.getLikes(limit, offset);
            call.enqueue(new RestCallback<BlogLikes>(this, "likes"));
        }
    }

    @Override
    public void getLikePosts(String name) {
        if (call == null) {
            if (blogService == null) {
                blogService = RestClient.getInstance().getBlogService();
            }
            this.name = name;
            HashMap<String, String> options = new HashMap<>(2);
            options.put("limit", "" + limit);
            options.put("offset", "" + offset);
            call = blogService.getBlogLikes(name,
                    DataManager.getInstance().getUsingTumblrApp().getApiKey(), options);
            call.enqueue(new RestCallback<BlogLikes>(this, "likes"));
        }
    }

    @Override
    public void nextLikePosts() {
        if (name != null) {
            getLikePosts(name);
        } else {
            getLikePosts();
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
        BlogLikes likes = (BlogLikes) baseBean.getResponse();
        offset += likes.getList().size();
        boolean hasNext = true;
        if (likes.getList().size() < limit || offset >= likes.getCount()) {
            hasNext = false;
        }
        //trim to only show videos and photos
        ArrayList<PostsItem> postsItems = new ArrayList<>(likes.getList().size());
        for (PostsItem item: likes.getList()) {
            if (TextUtils.equals(item.getType(), "video") ||
                    TextUtils.equals(item.getType(), "photo")) {
                postsItems.add(item);
            }
        }
        view.showLikePosts(postsItems, hasNext);
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
