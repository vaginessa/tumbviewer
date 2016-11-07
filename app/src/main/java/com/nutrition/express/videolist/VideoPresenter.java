package com.nutrition.express.videolist;

import android.util.Log;

import com.nutrition.express.application.Constants;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallBack;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogLikes;
import com.nutrition.express.model.rest.bean.BlogPosts;

import java.util.HashMap;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 5/17/16.
 */
public class VideoPresenter implements VideoContract.Presenter {
    private VideoContract.View view;
    private BlogService blogService;
    private int offset = 0, total;
    private boolean loading = false;

    public VideoPresenter(VideoContract.View view) {
        this.view = view;
        blogService = RestClient.getInstance().getBlogService();
    }

    private ResponseListener listener = new ResponseListener() {
        @Override
        public void onResponse(BaseBean baseBean, String tag) {
            if (view == null) {
                return;
            }
            loading = false;
            switch (tag) {
                case "posts":
                    BlogPosts blogPosts = (BlogPosts) baseBean.getResponse();
                    total = blogPosts.getCount();
                    offset += blogPosts.getList().size();
                    view.showData(blogPosts.getList().toArray(), offset < total);
                    if (blogPosts.getBlogInfo().isFollowed()) {
                        view.onFollowed();
                    }
                    break;
                case "likes":
                    BlogLikes blogLikes = (BlogLikes) baseBean.getResponse();
                    total = blogLikes.getCount();
                    offset += blogLikes.getList().size();
                    view.showData(blogLikes.getList().toArray(), offset < total);
                    break;
            }
            Log.d(TAG, "onResponse: " + total + ":" + offset);
        }

        @Override
        public void onFailure(String tag) {
            if (view == null) {
                return;
            }
            loading = false;
            if (offset == 0) {
                view.showLoadingFailure();
            } else {
                view.showLoadingNextFailure();
            }
        }
    };

    @Override
    public void loadData(String blogName, int type) {
        if (loading) {
            return;
        }
        loading = true;
        HashMap<String, String> para = new HashMap<>();
        para.put("offset", Integer.toString(offset));
        if (type == VideoListActivity.POSTS_VIDEO_DEFAULT) {
            Call<BaseBean<BlogPosts>> postsCall = blogService.getBlogPosts(blogName, "video",
                    Constants.CONSUMER_KEY, para);
            postsCall.enqueue(new RestCallBack<BlogPosts>(listener, "posts"));
        } else {
            //get likes blog
            Call<BaseBean<BlogLikes>> likesCall = blogService.getBlogLikes(blogName, Constants.CONSUMER_KEY,
                    para);
            likesCall.enqueue(new RestCallBack<BlogLikes>(listener, "likes"));
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        view = null;
    }
}
