package com.nutrition.express.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nutrition.express.R;
import com.nutrition.express.blogposts.PhotoPostVH;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.data.bean.PhotoPostsItem;
import com.nutrition.express.model.event.EventStatusBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huang on 11/2/16.
 */

public class DashboardFragment extends Fragment
        implements DashboardContract.View, CommonRVAdapter.OnLoadListener {
    private DashboardPresenter presenter;
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                    newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                Fresco.getImagePipeline().resume();
            } else {
                Fresco.getImagePipeline().pause();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 10 && status == View.VISIBLE) {
                //hide
                status = View.GONE;
                EventBus.getDefault().post(new EventStatusBar(status));
            } else if (dy < -10 && status == View.GONE) {
                //show
                status = View.VISIBLE;
                EventBus.getDefault().post(new EventStatusBar(status));
            }
        }
    };
    private boolean loaded = false;
    private int status = View.VISIBLE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (adapter == null) {
            adapter = getAdapter();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(onScrollListener);

        if (presenter == null) {
            presenter = new DashboardPresenter(this, getType());
        } else {
            presenter.onAttach(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!loaded) {
            getPosts();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDetach();
        }
        recyclerView.removeOnScrollListener(onScrollListener);
        recyclerView = null;
        refreshLayout = null;
    }

    @Override
    public void showDashboard(List<PhotoPostsItem> blogPosts, boolean hasNext) {
        loaded = true;
        if (blogPosts != null) {
            adapter.append(blogPosts.toArray(), hasNext);
        } else {
            adapter.append(null, hasNext);
        }
    }

    @Override
    public void resetData(List<PhotoPostsItem> blogPosts, boolean hasNext) {
        adapter.resetData(blogPosts.toArray(), hasNext);
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        adapter.showLoadingFailure(t.getMessage());
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(int code, String error) {
        adapter.showLoadingFailure(getString(R.string.load_failure_des, code, error));
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
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
        builder.addItemType(PhotoPostsItem.class, R.layout.item_post,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new PhotoPostVH(view);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    protected String getType() {
        return "photo";
    }

    private void getPosts() {
        if (presenter == null) {
            presenter = new DashboardPresenter(this, getType());
        }
        presenter.getDashboard();
    }

    public void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    public void refreshData() {
        loaded = false;
        adapter = getAdapter();
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
        presenter.refresh();
    }

}
