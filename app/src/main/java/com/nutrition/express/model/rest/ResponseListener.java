package com.nutrition.express.model.rest;

import com.nutrition.express.model.rest.bean.BaseBean;

/**
 * Created by huang on 2/18/16.
 */
public interface ResponseListener {
    void onResponse(BaseBean baseBean, String tag);
    void onFailure(String tag);
}
