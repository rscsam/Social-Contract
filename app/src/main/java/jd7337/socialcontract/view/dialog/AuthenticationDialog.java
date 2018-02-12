package jd7337.socialcontract.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.listener.InstagramAuthenticationListener;

/**
 * Created by Ali Khosravi on 1/22/2018.
 */


public class AuthenticationDialog extends Dialog {

    private final InstagramAuthenticationListener listener;
    private Context context;

    private WebView web_view;

    private final String url = R.string.instagram_base_url
            + "oauth/authorize/?client_id="
            + R.string.instagram_client_id
            + "&redirect_uri="
            + R.string.instagram_redirect_url
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
        web_view = (WebView) findViewById(R.id.web_view);
        web_view.loadUrl(url);
        System.out.println("iinit called");
        web_view.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("Page Started");
            }

            String access_token;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("#access_token=") && !authComplete) {
                    Uri uri = Uri.parse(url);
                    access_token = uri.getEncodedFragment();
                    // get the whole token after the '=' sign
                    access_token = access_token.substring(access_token.lastIndexOf("=")+1);
                    Log.i("", "CODE : " + access_token);
                    authComplete = true;
                    listener.onCodeReceived(access_token);
                    dismiss();

                } else if (url.contains("?error")) {
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
}
