package com.nutrition.express.login;

import android.os.Handler;
import android.util.Log;

import com.nutrition.express.application.Constants;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.helper.OAuth1SigningHelper;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.util.PreferencesUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 10/17/16.
 */

public class LoginPresenter implements LoginContract.LoginPresenter {
    private Handler handler = new Handler();
    private LoginContract.LoginView view;
    private OkHttpClient okHttpClient;
    private boolean requesting = false;
    private String oauthToken, oauthTokenSecret;

    public LoginPresenter(LoginContract.LoginView view) {
        this.view = view;
        okHttpClient = RestClient.getInstance().getOkHttpClient();
    }

    @Override
    public void getRequestToken() {
        if (!requesting) {
            requesting = true;
            String auth = new OAuth1SigningHelper()
                    .buildRequestHeader("POST", Constants.REQUEST_TOKEN_URL);
            Request request = new Request.Builder()
                    .url(Constants.REQUEST_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                    .header("Authorization", auth)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(requestTokenFailure);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        Log.d(TAG, "onResponse: " + body);
                        HashMap<String, String> hashMap = convert(body);
                        oauthToken = hashMap.get("oauth_token");
                        oauthTokenSecret = hashMap.get("oauth_token_secret");
                        handler.post(requestTokenSuccess);
                    } else {
                        handler.post(requestTokenFailure);
                    }
                }
            });
        }
    }

    @Override
    public void getAccessToken(String oauthVerifier) {
        if (!requesting) {
            requesting = true;
            String auth = new OAuth1SigningHelper()
                    .buildAccessHeader("POST", Constants.ACCESS_TOKEN_URL,
                            oauthToken, oauthVerifier, oauthTokenSecret);
            Request request = new Request.Builder()
                    .url(Constants.ACCESS_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                    .header("Authorization", auth)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(accessTokenFailure);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        Log.d(TAG, "onResponse: " + body);
                        HashMap<String, String> hashMap = convert(body);
                        oauthToken = hashMap.get("oauth_token");
                        oauthTokenSecret = hashMap.get("oauth_token_secret");
                        handler.post(accessTokenSuccess);
                    } else {
                        handler.post(accessTokenFailure);
                    }

                }
            });
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        view = null;
    }

    private Runnable requestTokenFailure = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.onError(0, "get request token failed");
            }
            requesting = false;
        }
    };

    private Runnable requestTokenSuccess = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.loadUrl(Constants.AUTHORIZE_URL + "?oauth_token=" + oauthToken);
            }
            requesting = false;
        }
    };

    private Runnable accessTokenFailure = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.onError(0, "get access token failed");
            }
            requesting = false;
        }
    };

    private Runnable accessTokenSuccess = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.showLoginSuccess();
                //save access token, and token secret
                PreferencesUtils.putString("access_token", oauthToken);
                DataManager.getInstance().loginSuccess(oauthToken, oauthTokenSecret);
            }
            requesting = false;
        }
    };

    private HashMap<String, String> convert(String body) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] strings = body.split("&");
        for (String string : strings) {
            String[] pair = string.split("=");
            if (pair.length == 2) {
                hashMap.put(pair[0], pair[1]);
            }
        }
        return hashMap;
    }

}
