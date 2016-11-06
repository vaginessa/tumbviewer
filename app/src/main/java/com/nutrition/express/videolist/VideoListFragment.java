package com.nutrition.express.videolist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;

/**
 * Created by huang on 5/26/16.
 */
public class VideoListFragment extends Fragment
        implements CommonRVAdapter.OnLoadListener, VideoContract.View {
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private int type;
    private String blogName;
    private boolean loaded = false;
    private VideoPresenter presenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        type = bundle.getInt("type");
        blogName = bundle.getString("blog_name");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !loaded) {
            getPostsVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !loaded) {
            getPostsVideo();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = buildAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    @Override
    public void showData(Object[] items, boolean autoLoadingNext) {
        loaded = true;
        adapter.append(items, autoLoadingNext);
    }

    @Override
    public void showLoadingFailure() {
        adapter.showLoadingFailure();
    }

    @Override
    public void showLoadingNextFailure() {
        adapter.showLoadingNextFailure();
    }

    @Override
    public void setPresenter(VideoContract.Presenter presenter) {

    }

    @Override
    public void retry() {
        getPostsVideo();
    }

    @Override
    public void loadNextPage() {
        getPostsVideo();
    }

    private void getPostsVideo() {
        if (presenter == null) {
            presenter = new VideoPresenter(this);
        }
        presenter.loadData(blogName, type);
    }

    private CommonRVAdapter buildAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_video,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new VideoVH(view);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

}
