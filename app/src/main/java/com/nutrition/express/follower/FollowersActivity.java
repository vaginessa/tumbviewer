package com.nutrition.express.follower;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nutrition.express.R;
import com.nutrition.express.model.rest.bean.Users;

public class FollowersActivity extends AppCompatActivity implements FollowersContract.FollowersView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        FollowersPresenter presenter = new FollowersPresenter(this);
        presenter.getFollowers("david.tumblr.com");
    }

    @Override
    public void showFollowers(Users users) {

    }

    @Override
    public void showFailure(String error) {

    }

    @Override
    public void setPresenter(FollowersContract.FollowersPresenter presenter) {

    }
}
