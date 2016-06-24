package com.nutrition.express.rest;

import com.nutrition.express.rest.bean.BaseBean;

/**
 * Created by huang on 2/18/16.
 */
public interface ResponseListener {
    void onResponse(BaseBean baseBean, String tag);
    void onFailure(String tag);
}
