package com.nutrition.express.likes;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.List;

/**
 * Created by huang on 11/8/16.
 */

public interface LikesContract {
    interface LikesPresenter extends BasePresenter {
        void getLikePosts();
        void nextLikePosts();
        void getLikePosts(String name);
    }

    interface LikesView extends BaseView {
        void showLikePosts(List<PostsItem> posts, boolean hasNext);
    }
}
