package com.nutrition.express.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.application.Constants;
import com.nutrition.express.main.MainActivity;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrApp;

import java.util.List;

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
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle("Login");

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView = (WebView) findViewById(R.id.webView);
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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
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
        showTumblrApps();
    }


    private void showTumblrApps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<TumblrApp> list = DataManager.getInstance().getTumblrAppList();
        String[] keys = new String[list.size()];
        int checkedItem = 0;
        for (int i = 0; i < keys.length; i++) {
            keys[i] = list.get(i).getApiKey();
            if (list.get(i).isUsing()) {
                checkedItem = i;
            }
        }
        builder.setSingleChoiceItems(keys, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DataManager.getInstance().setUsingTumblrApp(which)) {
                    DataManager.getInstance().logout();
                    loginPresenter.getRequestToken();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }


}
