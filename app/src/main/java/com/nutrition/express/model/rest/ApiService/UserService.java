package com.nutrition.express.model.rest.ApiService;

import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by huang on 10/18/16.
 */

public interface UserService {
    @GET("/v2/blog/{id}/followers")
    Call<BaseBean<Users>> getFollowers(@Path("id") String id,
                                       @Query("limit") int limit, @Query("offset") int offset);
}
