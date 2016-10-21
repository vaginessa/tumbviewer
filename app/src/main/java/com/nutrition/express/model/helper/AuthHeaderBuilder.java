package com.nutrition.express.model.helper;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.nutrition.express.application.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;
import static com.nutrition.express.application.Constants.REDIRECT_URI;

/**
 * Created by huang on 10/18/16.
 */

public final class AuthHeaderBuilder {
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";

    private final String oauth_callback = "oauth_callback";
    private final String oauth_consumer_key = "oauth_consumer_key";
    private final String oauth_nonce = "oauth_nonce";
    private final String oauth_signature_method = "oauth_signature_method";
    private final String oauth_signature = "oauth_signature";
    private final String oauth_timestamp = "oauth_timestamp";
    private final String oauth_token = "oauth_token";
    private final String oauth_verifier = "oauth_verifier";
    private final String oauth_version = "oauth_version";

    private final String signature_method = "HMAC-SHA1";
    private final String version = "1.0a";

    private final int nonce;
    private final long timestamp;
    private String token;
    private String verifier;
    private String callback;
    private String signature;

    public AuthHeaderBuilder() {
        nonce = (int) (Math.random() * 100000000);
        timestamp = System.currentTimeMillis() / 1000;
    }

    public String buildRequestHeader(@NonNull String method, @NonNull String url) {
        try {
            callback = URLEncoder.encode(REDIRECT_URI, ENC);
        } catch (UnsupportedEncodingException e) {
            callback = REDIRECT_URI;
        }
        signature = getSignature(Constants.CONSUMER_SECRET + "&",
                buildBaseString(method, url, buildParaString("=", "&")));
        return buildParaString("=\"", "\",");
    }

    public String buildAccessHeader(@NonNull String method, @NonNull String url,
                                    @NonNull String token, @NonNull String verifier,
                                    @NonNull String secret) {
        this.token = token;
        this.verifier = verifier;
        signature = getSignature(Constants.CONSUMER_SECRET + "&" + secret,
                buildBaseString(method, url, buildParaString("=", "&")));
        return buildParaString("=\"", "\",");
    }

    public String buildAuthHeader(@NonNull String method, @NonNull String url,
                                  @NonNull String token, @NonNull String secret) {
        this.token = token;
        Log.d(TAG, "buildAuthHeader: " + method + "---" + url);
        signature = getSignature(Constants.CONSUMER_SECRET + "&" + secret,
                buildBaseString(method, url, buildParaString("=", "&")));
        return buildParaString("=\"", "\",");
    }

    /**
     *
     * @param connector "=" or "=""
     * @param separator "&" or "","
     * @return
     */
    private String buildParaString(String connector, String separator) {
        StringBuilder builder = new StringBuilder();
        if (callback != null) {
            builder.append(oauth_callback);
            builder.append(connector);
            builder.append(callback);
            builder.append(separator);
        }
        builder.append(oauth_consumer_key);
        builder.append(connector);
        builder.append(Constants.CONSUMER_KEY);
        builder.append(separator);
        builder.append(oauth_nonce);
        builder.append(connector);
        builder.append(nonce);
        builder.append(separator);
        builder.append(oauth_signature_method);
        builder.append(connector);
        builder.append(signature_method);
        builder.append(separator);
        builder.append(oauth_timestamp);
        builder.append(connector);
        builder.append(timestamp);
        builder.append(separator);
        if (token != null) {
            builder.append(oauth_token);
            builder.append(connector);
            builder.append(token);
            builder.append(separator);
        }
        if (verifier != null) {
            builder.append(oauth_verifier);
            builder.append(connector);
            builder.append(verifier);
            builder.append(separator);
        }
        builder.append(oauth_version);
        builder.append(connector);
        builder.append(version);
        if (signature != null) {
            builder.append(separator);
            builder.append(oauth_signature);
            builder.append(connector);
            builder.append(signature);
        }
        return builder.toString();
    }

    private String buildBaseString(String method, String url, String para) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(method);
            builder.append("&");
            builder.append(URLEncoder.encode(url, ENC));
            builder.append("&");
            builder.append(URLEncoder.encode(para, ENC));
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String getSignature(String key, String text) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(ENC), HMAC_SHA1);
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(secretKey);
            byte[] result = mac.doFinal(text.getBytes(ENC));

            // encode it, base64 it, change it to string and return.
            String signature = Base64.encodeToString(result, Base64.NO_WRAP);
            signature = URLEncoder.encode(signature, ENC);
            return signature;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

}
