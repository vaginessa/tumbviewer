package com.nutrition.express.following;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.FollowingBlog;
import com.nutrition.express.videolist.VideoListActivity;

import java.util.List;

public class FollowingActivity extends AppCompatActivity
        implements FollowingContract.FollowersView, CommonRVAdapter.OnLoadListener {
    private CommonRVAdapter adapter;
    private FollowingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(FollowingBlog.Blog.class, R.layout.item_following_blog,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new BlogVH(view);
                    }
                });
        builder.setLoadListener(this);
        adapter = builder.build();
        recyclerView.setAdapter(adapter);

        presenter = new FollowingPresenter(this);
        presenter.getMyFollowing();
    }

    @Override
    public void retry() {
        presenter.getMyFollowing();
    }

    @Override
    public void loadNextPage() {
        presenter.getNextFollowing();
    }

    @Override
    public void showFollowing(List<FollowingBlog.Blog> blogs, boolean hasNext) {
        adapter.append(blogs.toArray(), hasNext);
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
    public void setPresenter(FollowingContract.FollowersPresenter presenter) {

    }

    public class BlogVH extends CommonViewHolder<FollowingBlog.Blog>
            implements View.OnClickListener {
        private TextView nameTV;
        private FollowingBlog.Blog blog;

        public BlogVH(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(FollowingBlog.Blog blog) {
            this.blog = blog;
            nameTV.setText(blog.getName());
        }

        @Override
        public void onClick(View v) {
           openBlog(blog.getName());
        }
    }

    private void openBlog(String name) {
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("blog_name", name);
        startActivity(intent);
    }
}
