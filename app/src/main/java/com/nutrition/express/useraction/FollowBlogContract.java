package com.nutrition.express.useraction;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 11/7/16.
 */

public interface FollowBlogContract {
    interface Presenter extends BasePresenter {
        void follow(String url);
        void unfollow(String url);
    }

    interface View extends BaseView<Presenter> {
        void onFollow();
        void onUnfollow();
    }
}
