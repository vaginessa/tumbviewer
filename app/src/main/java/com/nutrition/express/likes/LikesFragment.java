package com.nutrition.express.likes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;
import com.nutrition.express.blogposts.PostVH;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 11/8/16.
 */

public class LikesFragment extends Fragment
        implements LikesContract.LikesView, CommonRVAdapter.OnLoadListener {
    private CommonRVAdapter adapter;
    private LikesContract.LikesPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = getCommonRVAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String name = null;
        presenter = new LikesPresenter(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString("blog_name", null);
        }
        if (name == null) {
            presenter.getLikePosts();
        } else {
            presenter.getLikePosts(name);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    private CommonRVAdapter getCommonRVAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_post, new CommonRVAdapter.CreateViewHolder() {
            @Override
            public CommonViewHolder createVH(View view) {
                return new PostVH(view);
            }
        });
        builder.setLoadListener(this);
        return builder.build();
    }

    @Override
    public void retry() {
        presenter.getLikePosts();
    }

    @Override
    public void loadNextPage() {
        presenter.nextLikePosts();
    }

    @Override
    public void showLikePosts(List<PostsItem> posts, boolean hasNext) {
        //trim to only show videos and photos
        ArrayList<PostsItem> postsItems = new ArrayList<>(posts.size());
        for (PostsItem item: posts) {
            if (TextUtils.equals(item.getType(), "video") ||
                    TextUtils.equals(item.getType(), "photo")) {
                postsItems.add(item);
            }
        }
        adapter.append(postsItems.toArray(), hasNext);
    }

    @Override
    public void onLoadFailure() {
        adapter.showLoadingFailure();
    }

    @Override
    public void onLoadNextFailure() {
        adapter.showLoadingNextFailure();
    }

    @Override
    public void setPresenter(LikesContract.LikesPresenter presenter) {

    }
}
