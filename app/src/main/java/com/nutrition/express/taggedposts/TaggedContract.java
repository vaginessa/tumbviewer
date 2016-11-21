package com.nutrition.express.taggedposts;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.List;

/**
 * Created by huang on 11/21/16.
 */

public interface TaggedContract {
    interface Presenter extends BasePresenter {
        void getTaggedPosts(String tag);
        void nextTaggedPosts(String tag);
    }

    interface View extends BaseView {
        void showTaggedPosts(List<PostsItem> postsItems, boolean hasNext);
    }
}
