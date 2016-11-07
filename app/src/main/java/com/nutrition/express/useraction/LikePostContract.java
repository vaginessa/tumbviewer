package com.nutrition.express.useraction;

/**
 * Created by huang on 11/7/16.
 */

public interface LikePostContract {
    interface Presenter {
        void like(long id, String reblogKey);
        void unlike(long id, String reblogKey);
        void onDetach();
    }

    interface View {
        void onLike();
        void onLikeFailure();
        void onUnlike();
        void onUnlikeFailure();
    }
}
