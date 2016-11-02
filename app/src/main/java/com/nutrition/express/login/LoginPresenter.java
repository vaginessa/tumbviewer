package com.nutrition.express.login;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.nutrition.express.application.Constants;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.helper.OAuth1SigningHelper;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.util.PreferencesUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.nutrition.express.application.Constants.CONSUMER_KEY;
import static com.nutrition.express.application.Constants.REDIRECT_URI;

/**
 * Created by huang on 10/17/16.
 */

public class LoginPresenter implements LoginContract.LoginPresenter {
    private Handler handler = new Handler();
    private LoginContract.LoginView view;
    private OkHttpClient okHttpClient;
    private boolean requesting = false;
    private String oauthToken, oauthTokenSecret, oauthVerifier;

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";


    public LoginPresenter(LoginContract.LoginView view) {
        this.view = view;
        okHttpClient = RestClient.getInstance().getOkHttpClient();
    }

    private String getSignature(String key, String text)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        /**
         * base has three parts, they are connected by "&": 1) protocol 2) URL
         * (need to be URLEncoded) 3) Parameter List (need to be URLEncoded).
         */

        // yea, don't ask me why, it is needed to append a "&" to the end of
        // secret key.
        byte[] keyBytes = key.getBytes(ENC);

        SecretKey secretKey = new SecretKeySpec(keyBytes, HMAC_SHA1);

        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(secretKey);

        byte[] result = mac.doFinal(text.getBytes(ENC));

        // encode it, base64 it, change it to string and return.
        return Base64.encodeToString(result, Base64.NO_WRAP);
    }

    /**
     * build header access token
     * @return
     */
    private String buildAccessHeader() {
        int nonce = (int) (Math.random() * 100000000);
        long timestamp = System.currentTimeMillis() / 1000;

        String para = "oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + nonce
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_timestamp=" + timestamp
                + "&oauth_token=" + oauthToken
                + "&oauth_verifier=" + oauthVerifier
                + "&oauth_version=1.0a";

        StringBuilder builder = new StringBuilder();
        builder.append("oauth_consumer_key=\"");
        builder.append(CONSUMER_KEY);
        builder.append("\",oauth_nonce=\"");
        builder.append(nonce);
        builder.append("\",oauth_signature_method=\"HMAC-SHA1");
        builder.append("\",oauth_timestamp=\"");
        builder.append(timestamp);
        builder.append("\",oauth_token=\"");
        builder.append(oauthToken);
        builder.append("\",oauth_verifier=\"");
        builder.append(oauthVerifier);
        builder.append("\",oauth_version=\"1.0a\"");

        try {
            StringBuilder base = new StringBuilder();
            base.append("POST&");
            base.append(URLEncoder.encode(Constants.ACCESS_TOKEN_URL, ENC));
            base.append("&");
            base.append(URLEncoder.encode(para, ENC));
            String signature = getSignature(Constants.CONSUMER_SECRET + "&" + oauthTokenSecret,
                    base.toString());
            builder.append(",oauth_signature=\"");
            builder.append(URLEncoder.encode(signature, ENC));
            builder.append("\"");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
        }

        return builder.toString();
    }

    /**
     * build header request token
     * @return
     */
    private String buildAuthHeader() {
        int nonce = (int) (Math.random() * 100000000);
        long timestamp = System.currentTimeMillis() / 1000;
        String callback;
        try {
            callback = URLEncoder.encode(REDIRECT_URI, ENC);
        } catch (UnsupportedEncodingException e) {
            callback = REDIRECT_URI;
        }

        String para = "oauth_callback=" + callback
                + "&oauth_consumer_key=" + CONSUMER_KEY
                + "&oauth_nonce=" + nonce
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_timestamp=" + timestamp
                + "&oauth_version=1.0a";

        StringBuilder builder = new StringBuilder();
        builder.append("oauth_callback=\"");
        builder.append(callback);
        builder.append("\",oauth_consumer_key=\"");
        builder.append(CONSUMER_KEY);
        builder.append("\",oauth_nonce=\"");
        builder.append(nonce);
        builder.append("\",oauth_signature_method=\"HMAC-SHA1");
        builder.append("\",oauth_timestamp=\"");
        builder.append(timestamp);
        builder.append("\",oauth_version=\"1.0a\"");

        try {
            StringBuilder base = new StringBuilder();
            base.append("POST&");
            base.append(URLEncoder.encode(Constants.REQUEST_TOKEN_URL, ENC));
            base.append("&");
            base.append(URLEncoder.encode(para, ENC));
            Log.d(TAG, "buildAuthHeader: " + base.toString());
            String signature = getSignature(Constants.CONSUMER_SECRET + "&", base.toString());
            builder.append(",oauth_signature=\"");
            builder.append(URLEncoder.encode(signature, ENC));
            builder.append("\"");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
        }

        return builder.toString();
    }

    @Override
    public void getRequestToken() {
        if (!requesting) {
            requesting = true;
            String auth = new OAuth1SigningHelper()
                    .buildRequestHeader("POST", Constants.REQUEST_TOKEN_URL);
            Request request = new Request.Builder()
                    .url(Constants.REQUEST_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "empty"))
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
        this.oauthVerifier =  oauthVerifier;
        if (!requesting) {
            requesting = true;
            String auth = new OAuth1SigningHelper()
                    .buildAccessHeader("POST", Constants.ACCESS_TOKEN_URL,
                            oauthToken, oauthVerifier, oauthTokenSecret);
            Request request = new Request.Builder()
                    .url(Constants.ACCESS_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "empty"))
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
                view.showLoginFailure("get request token failed");
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
                view.showLoginFailure("get access token failed");
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
                PreferencesUtils.putDefaultString("access_token", oauthToken);
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
