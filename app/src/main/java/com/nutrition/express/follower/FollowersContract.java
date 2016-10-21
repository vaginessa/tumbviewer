package com.nutrition.express.follower;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.Users;

/**
 * Created by huang on 10/18/16.
 */

public interface FollowersContract {
    interface FollowersPresenter extends BasePresenter {
        void getFollowers(String id);
    }

    interface FollowersView extends BaseView<FollowersPresenter> {
        void showFollowers(Users users);
        void showFailure(String error);
    }
}
