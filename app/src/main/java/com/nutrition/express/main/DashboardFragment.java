package com.nutrition.express.main;

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
import com.nutrition.express.blogposts.VideoVH;

import java.util.List;

/**
 * Created by huang on 11/2/16.
 */

public class DashboardFragment extends Fragment
        implements DashboardContract.View, CommonRVAdapter.OnLoadListener {
    private DashboardPresenter presenter;
    private CommonRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPosts();
    }

    @Override
    public void showDashboard(List<PostsItem> blogPosts, boolean hasNext) {
        adapter.append(blogPosts.toArray(), hasNext);
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
    public void setPresenter(DashboardContract.Presenter presenter) {

    }

    @Override
    public void retry() {
        getPosts();
    }

    @Override
    public void loadNextPage() {
        getPosts();
    }

    protected CommonRVAdapter getAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_video, new CommonRVAdapter.CreateViewHolder() {
            @Override
            public CommonViewHolder createVH(View view) {
                return new VideoVH(view);
            }
        });
        builder.setLoadListener(this);
        return builder.build();
    }

    protected String getType() {
        return "video";
    }

    private void getPosts() {
        if (presenter == null) {
            presenter = new DashboardPresenter(this, getType());
        }
        presenter.getDashboard();
    }

}
