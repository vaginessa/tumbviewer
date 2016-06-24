package com.nutrition.express.videolist;

import com.nutrition.express.application.Constant;
import com.nutrition.express.rest.ApiService.BlogService;
import com.nutrition.express.rest.ResponseListener;
import com.nutrition.express.rest.RestCallBack;
import com.nutrition.express.rest.RestClient;
import com.nutrition.express.rest.bean.BaseBean;
import com.nutrition.express.rest.bean.BlogInfo;
import com.nutrition.express.rest.bean.BlogInfoItem;
import com.nutrition.express.rest.bean.BlogLikes;
import com.nutrition.express.rest.bean.BlogPosts;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by huang on 5/17/16.
 */
public class VideoPresenter {
    private BlogService blogService;

    public VideoPresenter() {
        init();
    }

    private void init() {
        blogService = RestClient.getInstance().getBlogService();
        //get blog info
        Call<BaseBean<BlogInfo>> infoCall = blogService.getBlogInfo(Constant.TEST_USER, Constant.API_KEY);
        infoCall.enqueue(new RestCallBack<BlogInfo>(listener, "info"));

        //get likes blog
        Call<BaseBean<BlogLikes>> likesCall = blogService.getBlogLikes(Constant.TEST_USER, Constant.API_KEY,
                new HashMap<String, String>());
        likesCall.enqueue(new RestCallBack<BlogLikes>(listener, "likes"));

        Call<BaseBean<BlogPosts>> postsCall = blogService.getBlogPosts(Constant.TEST_USER, "video", Constant.API_KEY,
                new HashMap<String, String>());
        postsCall.enqueue(new RestCallBack<BlogPosts>(listener, "posts"));
    }

    private ResponseListener listener = new ResponseListener() {
        @Override
        public void onResponse(BaseBean baseBean, String tag) {
            switch (tag) {
                case "info":
                    BlogInfo blogInfo = (BlogInfo) baseBean.getResponse();
                    BlogInfoItem item = blogInfo.getItem();
                    Logger.d("info", item);
                    break;
                case "likes":
                    BlogLikes blogLikes = (BlogLikes) baseBean.getResponse();
                    break;
                case "posts":
                    BlogPosts blogPosts = (BlogPosts) baseBean.getResponse();
                    Logger.d("posts", blogPosts);
                    break;
            }
        }

        @Override
        public void onFailure(String tag) {
            Logger.e("failure", tag);
        }
    };

}
