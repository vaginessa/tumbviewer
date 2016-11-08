package com.nutrition.express.following;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.FollowingBlog;
import com.nutrition.express.util.FrescoUtils;
import com.nutrition.express.blogposts.PostListActivity;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        private TextView nameTV, titleTV, updateTime;
        private SimpleDraweeView avatarView;
        private FollowingBlog.Blog blog;

        public BlogVH(View itemView) {
            super(itemView);
            avatarView = (SimpleDraweeView) itemView.findViewById(R.id.blog_avatar);
            updateTime = (TextView) itemView.findViewById(R.id.blog_last_update);
            nameTV = (TextView) itemView.findViewById(R.id.blog_name);
            titleTV = (TextView) itemView.findViewById(R.id.blog_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(FollowingBlog.Blog blog) {
            this.blog = blog;
            nameTV.setText(blog.getName());
            titleTV.setText(blog.getTitle());
            FrescoUtils.setTumblrAvatarUri(avatarView, blog.getName(), 128);
            updateTime.setText(
                    itemView.getResources().getString(R.string.update_des,
                            DateUtils.getRelativeTimeSpanString(blog.getUpdated() * 1000,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS)
                    ));
        }

        @Override
        public void onClick(View v) {
           openBlog(blog.getName());
        }
    }

    private void openBlog(String name) {
        Intent intent = new Intent(this, PostListActivity.class);
        intent.putExtra("blog_name", name);
        startActivity(intent);
    }
}
