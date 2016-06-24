package com.nutrition.express.videolist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;
import com.nutrition.express.application.Constant;
import com.nutrition.express.rest.ApiService.BlogService;
import com.nutrition.express.rest.ResponseListener;
import com.nutrition.express.rest.RestCallBack;
import com.nutrition.express.rest.RestClient;
import com.nutrition.express.rest.bean.BaseBean;
import com.nutrition.express.rest.bean.BlogLikes;
import com.nutrition.express.rest.bean.BlogPosts;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by huang on 5/26/16.
 */
public class VideoListFragment extends Fragment implements OnLoadListener {
    private BlogService blogService;

    private RecyclerView recyclerView;
    private VideoRVAdapter adapter;
    private int type, offset = 0, total;
    private String blogName;
    private boolean loaded = false, loading = false;

    //TODO: building a Presenter, MVP;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        blogService = RestClient.getInstance().getBlogService();
        Bundle bundle = getArguments();
        type = bundle.getInt("type");
        blogName = bundle.getString("blog_name");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !loaded) {
            getPostsVideo(blogName, type);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !loaded) {
            getPostsVideo(blogName, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoRVAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void loadPostsAgain() {
        getPostsVideo(blogName, type);
    }

    @Override
    public void loadPostsNext() {
        getPostsVideo(blogName, type);
    }

    private void getPostsVideo(String blogName, int type) {
        if (loading) {
            return;
        }
        HashMap<String, String> para = new HashMap<>();
        para.put("offset", Integer.toString(offset));
        if (type == VideoListActivity.POSTS_VIDEO_DEFAULT) {
            Call<BaseBean<BlogPosts>> postsCall = blogService.getBlogPosts(blogName, "video",
                    Constant.API_KEY, para);
            postsCall.enqueue(new RestCallBack<BlogPosts>(responseListener, "posts"));
        } else {
            //get likes blog
            Call<BaseBean<BlogLikes>> likesCall = blogService.getBlogLikes(blogName, Constant.API_KEY,
                    para);
            likesCall.enqueue(new RestCallBack<BlogLikes>(responseListener, "likes"));
        }
        loading = true;
    }

    private ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onResponse(BaseBean baseBean, String tag) {
            switch (tag) {
                case "posts":
                    BlogPosts blogPosts = (BlogPosts) baseBean.getResponse();
                    total = blogPosts.getCount();
                    offset += blogPosts.getList().size();
                    adapter.setPostsList(blogPosts.getList(), offset >= total);
                    loaded = true;
                    logInfo(total, blogPosts.getList().size());
                    break;
                case "likes":
                    BlogLikes blogLikes = (BlogLikes) baseBean.getResponse();
                    total = blogLikes.getCount();
                    offset += blogLikes.getList().size();
                    adapter.setPostsList(blogLikes.getList(), offset >= total);
                    loaded = true;
                    logInfo(total, blogLikes.getList().size());
                    break;
            }
            loading = false;
        }

        @Override
        public void onFailure(String tag) {
            adapter.loadPostsFailed();
            loading = false;
        }
    };

    private void logInfo(int total, int size) {
        Log.d(getTag(), "total video count :" + total);
        Log.d(getTag(), "load video count :" + size);
        Log.d(getTag(), "<--->");
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }
}
