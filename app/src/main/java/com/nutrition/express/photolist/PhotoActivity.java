package com.nutrition.express.photolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nutrition.express.R;
import com.nutrition.express.model.rest.bean.BlogPosts;

public class PhotoActivity extends AppCompatActivity implements PhotoContract.PhotoView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        PhotoPresenter presenter = new PhotoPresenter(this);
        presenter.getPhotos("guanbo");
    }

    @Override
    public void setPresenter(PhotoContract.PhotoPresenter presenter) {

    }

    @Override
    public void showPhotos(BlogPosts posts) {

    }
}
