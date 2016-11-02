package com.nutrition.express.model.rest.ApiService;

import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.BlogPosts;
import com.nutrition.express.model.rest.bean.FollowingBlog;
import com.nutrition.express.model.rest.bean.UserInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

/**
 * Created by huang on 10/18/16.
 */

public interface UserService {
    @GET("/v2/user/following")
    Call<BaseBean<FollowingBlog>> getFollowing(@Header("Authorization") String auth,
                                               @QueryMap Map<String, String> options);

    @GET("/v2/user/info")
    Call<BaseBean<UserInfo>> getInfo(@Header("Authorization") String auth);

    @GET("/v2/user/dashboard")
    Call<BaseBean<BlogPosts>> getDashboard(@Header("Authorization") String auth,
                                           @QueryMap Map<String, String> options);
}
