package com.huang.humblr.rest;

import com.huang.humblr.rest.bean.BaseBean;

/**
 * Created by huang on 2/18/16.
 */
public interface ResponseListener {
    void onResponse(BaseBean baseBean, String tag);
    void onFailure(String tag);
}
