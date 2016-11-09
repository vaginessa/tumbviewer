package com.nutrition.express.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.application.Constants;
import com.nutrition.express.main.MainActivity;
import com.nutrition.express.model.data.DataManager;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {
    private WebView webView;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataManager.getInstance().isLogin()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle("Login");

        webView = (WebView) findViewById(R.id.webView_login);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("webView url", url);
                if (url.startsWith(Constants.REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    if (!TextUtils.isEmpty(oauthVerifier)) {
                        loginPresenter.getAccessToken(oauthVerifier);
                        return true;
                    }
                    return false;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        loginPresenter = new LoginPresenter(this);
        loginPresenter.getRequestToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginPresenter != null) {
            loginPresenter.onDetach();
        }
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void showLoginSuccess() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
        //// TODO: 10/18/16 show a failure view
    }

    @Override
    public void onError(int code, String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        //// TODO: 10/18/16 show a failure view
    }

}
