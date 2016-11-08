package com.nutrition.express.blogposts;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 7/13/16.
 */

public interface PostContract {
    interface Presenter extends BasePresenter {
        void loadData(String blogName, int type);
    }

    interface View extends BaseView<Presenter> {
        void showData(Object[] items, boolean autoLoadingNext);
        void showLoadingFailure();
        void showLoadingNextFailure();
        void onFollowed();
    }
}
