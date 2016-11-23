package com.nutrition.express.model.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.nutrition.express.application.Constants.REDIRECT_URI;

/**
 * Created by huang on 10/18/16.
 */

public final class OAuth1SigningHelper {
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";

    private static final String OAUTH_CALLBACK = "oauth_callback";
    private static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private static final String OAUTH_NONCE = "oauth_nonce";
    private static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private static final String OAUTH_SIGNATURE = "oauth_signature";
    private static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    private static final String OAUTH_TOKEN = "oauth_token";
    private static final String OAUTH_VERIFIER = "oauth_verifier";
    private static final String OAUTH_VERSION = "oauth_version";

    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String VERSION_CODE = "1.0a";

    private TreeMap<String, String> oauthMap = new TreeMap<>();
    private String signature;
    private TumblrApp app;

    public OAuth1SigningHelper() {
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonce = timestamp;
        app = DataManager.getInstance().getUsingTumblrApp();
        oauthMap.put(OAUTH_CONSUMER_KEY, app.getApiKey());
        oauthMap.put(OAUTH_NONCE, nonce);
        oauthMap.put(OAUTH_TIMESTAMP, timestamp);
        oauthMap.put(OAUTH_SIGNATURE_METHOD, SIGNATURE_METHOD);
        oauthMap.put(OAUTH_VERSION, VERSION_CODE);
    }

    public String buildRequestHeader(@NonNull String method, @NonNull String url) {
        try {
            oauthMap.put(OAUTH_CALLBACK, REDIRECT_URI);
            signature = getSignature(app.getApiSecret() + "&",
                    buildBaseString(method, url, oauthMap));
            oauthMap.put(OAUTH_SIGNATURE, signature);
            return getAuthString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public String buildAccessHeader(@NonNull String method, @NonNull String url,
                                    @NonNull String token, @NonNull String verifier,
                                    @NonNull String secret) {
        try {
            oauthMap.put(OAUTH_TOKEN, token);
            oauthMap.put(OAUTH_VERIFIER, verifier);
            signature = getSignature(app.getApiSecret() + "&" + secret,
                    buildBaseString(method, url, oauthMap));
            oauthMap.put(OAUTH_SIGNATURE, signature);
            return getAuthString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public String buildAuthHeader(@NonNull String method, @NonNull String url,
                                  @NonNull String token, @NonNull String secret,
                                  @Nullable Map<String, String> map) {
        try {
            oauthMap.put(OAUTH_TOKEN, token);
            TreeMap<String, String> treeMap = new TreeMap<>(oauthMap);
            if (map != null && map.size() > 0) {
                treeMap.putAll(map);
            }
            signature = getSignature(app.getApiSecret() + "&" + secret,
                    buildBaseString(method, url, treeMap));
            oauthMap.put(OAUTH_SIGNATURE, signature);
            return getAuthString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String getAuthString() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append("OAuth ");
        for (Map.Entry<String, String> entry : oauthMap.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=\"");
            builder.append(URLEncoder.encode(entry.getValue(), ENC));
            builder.append("\",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String buildBaseString(String method, String url, TreeMap<String, String> baseMap)
            throws UnsupportedEncodingException{
        StringBuilder paraBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : baseMap.entrySet()) {
            paraBuilder.append(URLEncoder.encode(entry.getKey(), ENC));
            paraBuilder.append("=");
            paraBuilder.append(URLEncoder.encode(entry.getValue(), ENC));
            paraBuilder.append("&");
        }
        paraBuilder.deleteCharAt(paraBuilder.length() - 1);

        StringBuilder builder = new StringBuilder();
        builder.append(method);
        builder.append("&");
        builder.append(URLEncoder.encode(url, ENC));
        builder.append("&");
        builder.append(URLEncoder.encode(paraBuilder.toString(), ENC));
        return builder.toString();
    }

    private String getSignature(String key, String text) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(ENC), HMAC_SHA1);
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(secretKey);
            byte[] result = mac.doFinal(text.getBytes(ENC));

            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

}
