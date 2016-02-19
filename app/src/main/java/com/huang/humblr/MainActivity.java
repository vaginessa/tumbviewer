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

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private BlogService blogService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blogService = RestClient.getInstance().getBlogService();
        Call<BaseBean<BlogInfo>> infoCall = blogService.getBlogInfo("momo1234560.tumblr.com",
                "fuiKNFp9vQFvjLNvx4sUwti4Yb5yGutBN4Xh10LXZhhRKjWlV4");
        infoCall.enqueue(new RestCallBack<BlogInfo>(listener, "info"));

        Call<BaseBean<BlogLikes>> likesCall = blogService.getBlogLikes("momo1234560.tumblr.com",
                "fuiKNFp9vQFvjLNvx4sUwti4Yb5yGutBN4Xh10LXZhhRKjWlV4", null);
        likesCall.enqueue(new RestCallBack<BlogLikes>(listener, "likes"));

        Call<BaseBean<BlogPosts>> postsCall = blogService.getBlogPosts("momo1234560.tumblr.com",
                "text", "fuiKNFp9vQFvjLNvx4sUwti4Yb5yGutBN4Xh10LXZhhRKjWlV4", null);
        postsCall.enqueue(new RestCallBack<BlogPosts>(listener, "posts"));
    }

    private ResponseListener listener = new ResponseListener() {
        @Override
        public void onResponse(BaseBean baseBean, String tag) {
            switch (tag) {
                case "info":
                    BlogInfo blogInfo = (BlogInfo) baseBean.getResponse();
                    BlogInfoItem item = blogInfo.getItem();
                    Log.d("blog", "title-" + item.getTitle());
                    Log.d("blog", "posts-" + item.getPosts());
                    Log.d("blog", "name-" + item.getName());
                    Log.d("blog", "updated-" + item.getUpdated());
                    Log.d("blog", "des-" + item.getDescription());
                    Log.d("blog", "ask-" + item.isAsk());
                    Log.d("blog", "ask_anon-" + item.isAsk_anon());
                    Log.d("blog", "likes-" + item.getLikes());
                    break;
                case "likes":
                    BlogLikes blogLikes = (BlogLikes) baseBean.getResponse();
                    Log.d("blog", "likes count-" + blogLikes.getCount());
                    break;
                case "posts":
                    break;
            }
        }

        @Override
        public void onFailure(String tag) {
        }
    };

}
