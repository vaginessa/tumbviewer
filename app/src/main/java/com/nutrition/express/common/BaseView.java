package com.nutrition.express.common;

/**
 * Created by huang on 7/13/16.
 */

public interface BaseView {
    void onFailure(Throwable t);
    void onError(int code, String error);
}
