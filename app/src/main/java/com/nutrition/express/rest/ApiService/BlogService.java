package com.nutrition.express.rest.ApiService;

import com.nutrition.express.rest.bean.BaseBean;
import com.nutrition.express.rest.bean.BlogInfo;
import com.nutrition.express.rest.bean.BlogLikes;
import com.nutrition.express.rest.bean.BlogPosts;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by huang on 2/17/16.
 */
public interface BlogService {
    @GET("/v2/blog/{id}/info")
    Call<BaseBean<BlogInfo>> getBlogInfo(@Path("id") String id, @Query("api_key") String key);

    @GET("/v2/blog/{id}/likes")
    Call<BaseBean<BlogLikes>> getBlogLikes(@Path("id") String id, @Query("api_key") String key,
                                 @QueryMap HashMap<String, String> hashMap);

    @GET("/v2/blog/{id}/posts/{type}")
    Call<BaseBean<BlogPosts>> getBlogPosts(@Path("id") String id, @Path("type") String type,
                                           @Query("api_key") String key,
                                           @QueryMap HashMap<String, String> hashMap);
}
