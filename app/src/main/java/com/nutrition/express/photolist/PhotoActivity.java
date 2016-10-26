package com.nutrition.express.photolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.BlogPosts;
import com.nutrition.express.model.rest.bean.PostsItem;

public class PhotoActivity extends AppCompatActivity implements PhotoContract.PhotoView,
        CommonRVAdapter.OnLoadListener {
    private CommonRVAdapter adapter;
    private PhotoContract.PhotoPresenter presenter;
    private String name = "guanbo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.setLoadListener(this);
        builder.addItemType(PostsItem.class, R.layout.item_photo,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new PhotoVH(view);
                    }
                });
        adapter = builder.build();
        recyclerView.setAdapter(adapter);

        presenter = new PhotoPresenter(this);
        presenter.getPhotos(name);
    }

    @Override
    public void setPresenter(PhotoContract.PhotoPresenter presenter) {

    }

    @Override
    public void showPhotos(BlogPosts posts, boolean autoLoadingNext) {
        adapter.append(posts.getList().toArray(), autoLoadingNext);
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
    public void retry() {
        presenter.getPhotos(name);
    }

    @Override
    public void loadNextPage() {

    }
}
