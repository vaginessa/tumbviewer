package com.nutrition.express.photolist;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.BlogPosts;

/**
 * Created by huang on 10/19/16.
 */

public interface PhotoContract {
    interface PhotoPresenter extends BasePresenter {
        void getPhotos(String id);
    }

    interface PhotoView extends BaseView<PhotoPresenter> {
        void showPhotos(BlogPosts posts);
    }
}
