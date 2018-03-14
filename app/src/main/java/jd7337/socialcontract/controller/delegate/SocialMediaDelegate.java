package jd7337.socialcontract.controller.delegate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.net.URL;

/**
 * Created by sam on 2/24/18.
 */

public class SocialMediaDelegate {

    public static void receiveBitmapFromInstagram(Context context, final String url,
                                           final OnBitmapReceivedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL realUrl = new URL(url);
                    final Bitmap bitmap = BitmapFactory.decodeStream(realUrl.openStream());
                    listener.onBitmapReceived(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    public interface OnBitmapReceivedListener {
        void onBitmapReceived(Bitmap response);
    }
}
