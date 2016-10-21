package com.nutrition.express.videolist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;
import com.nutrition.express.util.Utils;

/**
 * Created by huang on 5/26/16.
 */
public class VideoListFragment extends Fragment
        implements CommonRVAdapter.OnLoadListener, VideoContract.View {
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private int defaultWidth;
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

        int pxOf16dp = (int) Utils.dp2Pixels(context, 16);
        defaultWidth = ExpressApplication.width - 2 * pxOf16dp;
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

    public void openBlog(String blogName) {
        if (!TextUtils.equals(blogName, this.blogName)) {
            Intent intent = new Intent(getActivity(), VideoListActivity.class);
            intent.putExtra("blog_name", blogName);
            getActivity().startActivity(intent);
        }
    }

    private CommonRVAdapter buildAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_video_1,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder onCreateVH(View view) {
                        return new VideoVH(view);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    private class VideoVH extends CommonViewHolder<PostsItem> implements View.OnClickListener {
        private SimpleDraweeView draweeView;
        private TextView blogName;
        private TextView noteCount;
        private String videoUrl;

        public VideoVH(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
            if (draweeView != null) {
                draweeView.setOnClickListener(this);
            }
            blogName = (TextView) itemView.findViewById(R.id.blog_name);
            blogName.setOnClickListener(this);
            noteCount = (TextView) itemView.findViewById(R.id.note_count);
        }

        @Override
        public void bindView(PostsItem postsItem) {
            videoUrl = postsItem.getVideo_url();
            String url = postsItem.getThumbnail_url();
            ViewGroup.LayoutParams params = draweeView.getLayoutParams();
            params.width = defaultWidth;
            if (postsItem.getThumbnail_width() > 0 &&
                    postsItem.getThumbnail_width() > postsItem.getThumbnail_height()) {
                params.height = params.width *
                        postsItem.getThumbnail_height() / postsItem.getThumbnail_width();
            } else {
                params.height = defaultWidth / 2;
            }
            draweeView.setLayoutParams(params);
            draweeView.setImageURI(url != null ? Uri.parse(url) : Uri.EMPTY);
            blogName.setText(postsItem.getBlog_name());
            noteCount.setText(postsItem.getNote_count() + "  热度");
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.simpleDraweeView) {
                openBottomSheet(videoUrl);
            } else if (v.getId() == R.id.blog_name) {
                openBlog(blogName.getText().toString());
            }
        }

        private void openBottomSheet(String url) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            VideoBottomSheet bottomSheet = new VideoBottomSheet();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(getActivity().getSupportFragmentManager(),
                    bottomSheet.getTag());
        }
    }
}
