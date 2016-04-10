package com.huang.humblr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.huang.humblr.rest.ApiService.BlogService;
import com.huang.humblr.rest.ResponseListener;
import com.huang.humblr.rest.RestCallBack;
import com.huang.humblr.rest.RestClient;
import com.huang.humblr.rest.bean.BaseBean;
import com.huang.humblr.rest.bean.BlogInfo;
import com.huang.humblr.rest.bean.BlogInfoItem;
import com.huang.humblr.rest.bean.BlogLikes;
import com.huang.humblr.rest.bean.BlogPosts;
import com.orhanobut.logger.Logger;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "fuiKNFp9vQFvjLNvx4sUwti4Yb5yGutBN4Xh10LXZhhRKjWlV4";
    private static final String TEST_USER = "momo1234560.tumblr.com";
    private BlogService blogService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blogService = RestClient.getInstance().getBlogService();
        //get blog info
        Call<BaseBean<BlogInfo>> infoCall = blogService.getBlogInfo(TEST_USER, API_KEY);
        infoCall.enqueue(new RestCallBack<BlogInfo>(listener, "info"));

        //get likes blog
        Call<BaseBean<BlogLikes>> likesCall = blogService.getBlogLikes(TEST_USER, API_KEY, null);
        likesCall.enqueue(new RestCallBack<BlogLikes>(listener, "likes"));

        Call<BaseBean<BlogPosts>> postsCall = blogService.getBlogPosts(TEST_USER, "text", API_KEY, null);
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
                    Log.d("blog", "likes count-" + blogLikes.getCount());
                    break;
                case "posts":
                    BlogPosts blogPosts = (BlogPosts) baseBean.getResponse();
                    Logger.d("posts", blogPosts);
                    break;
            }
        }

        @Override
        public void onFailure(String tag) {
        }
    };

}
