package com.nutrition.express.blogposts;

import android.text.TextUtils;

import com.nutrition.express.model.data.bean.PhotoPostsItem;
import com.nutrition.express.model.data.bean.VideoPostsItem;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogPosts;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

/**
 * Created by huang on 5/17/16.
 */
public class PostPresenter implements PostContract.Presenter, ResponseListener {
    private PostContract.View view;
    private BlogService blogService;
    private Call<BaseBean<BlogPosts>> call;
    private final int limit = 20;
    private int offset = 0;

    public PostPresenter(PostContract.View view) {
        this.view = view;
        blogService = RestClient.getInstance().getBlogService();
    }

    @Override
    public void loadData(String blogName) {
        if (call == null) {
            HashMap<String, String> para = new HashMap<>();
            para.put("limit", Integer.toString(limit));
            para.put("offset", Integer.toString(offset));
            call = blogService.getBlogPosts(blogName, "", para);
            call.enqueue(new RestCallback<BlogPosts>(this, "posts"));
        }
    }

    @Override
    public void onAttach(PostContract.View view) {

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
        BlogPosts blogPosts = (BlogPosts) baseBean.getResponse();
        offset += blogPosts.getList().size();
        boolean hasNext = true;
        if (blogPosts.getList().size() < limit || offset >= blogPosts.getCount()) {
            hasNext = false;
        }
        boolean isAdmin = blogPosts.getBlogInfo().isAdmin();
        if (isAdmin) {
            view.hideFollowItem();
        } else if (blogPosts.getBlogInfo().isFollowed()) {
            view.onFollowed();
        }
        //trim to only show videos and photos
        List<PhotoPostsItem> postsItems = new ArrayList<>(blogPosts.getList().size());
        for (PostsItem item: blogPosts.getList()) {
            item.setAdmin(isAdmin);
            if (TextUtils.equals(item.getType(), "video")) {
                postsItems.add(new VideoPostsItem(item));
            } else if (TextUtils.equals(item.getType(), "photo")) {
                postsItems.add(new PhotoPostsItem(item));
            }
        }
        view.showData(postsItems.toArray(), hasNext);
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
