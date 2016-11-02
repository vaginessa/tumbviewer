package com.nutrition.express.main;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.List;

/**
 * Created by huang on 11/2/16.
 */

public interface DashboardContract {
    interface Presenter extends BasePresenter {
        void getDashboard();
        void getNextDashboard();
    }

    interface View extends BaseView<Presenter> {
        void showDashboard(List<PostsItem> blogPosts, boolean hasNext);
        void showLoadingFailure();
        void showLoadingNextFailure();
    }

}
