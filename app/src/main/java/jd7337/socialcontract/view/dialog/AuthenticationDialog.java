package jd7337.socialcontract.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.listener.InstagramAuthenticationListener;

/**
 * Created by Ali Khosravi on 1/22/2018.
 *
 */


public class AuthenticationDialog extends Dialog {

    private final InstagramAuthenticationListener listener;
    private Context context;

    private final String url = getContext().getString(R.string.instagram_base_url)
            + "oauth/authorize/?client_id="
            + getContext().getString(R.string.instagram_client_id)
            + "&redirect_uri="
            + getContext().getString(R.string.instagram_redirect_url)
            + "&response_type=token"
            + "&display=touch&scope=public_content";

    public AuthenticationDialog(@NonNull Context context, InstagramAuthenticationListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        WebView web_view = findViewById(R.id.web_view);
        web_view.loadUrl(url);
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            String access_token;

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("#access_token=")) {
                    dismiss();
                    Uri uri = Uri.parse(url);
                    access_token = uri.getEncodedFragment();
                    // get the whole token after the '=' sign
                    access_token = access_token.substring(access_token.lastIndexOf("=")+1);
                    Log.i("", "CODE : " + access_token);
                    listener.onInstagramAuthTokenReceived(access_token);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(null);
                } else if (url.contains("?error")) {
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
}
