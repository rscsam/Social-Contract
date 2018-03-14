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
 *
 */

public class SocialMediaDelegate {

    public static void receiveBitmapFromInstagram(Context context, final String url,
                                           final OnBitmapReceivedListener listener) {
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        //String url = "https://api.instagram.com/v1/media/" + mediaId + "?access_token=" + accessToken;
//        // Initialize a new ImageRequest
//        url = url.substring(0, 4) + url.substring(5);
//        System.out.println(url);
//        ImageRequest imageRequest = new ImageRequest(
//                url, // Image URL
//                new Response.Listener<Bitmap>() { // Bitmap listener
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        System.out.println("hi");
//                        listener.onBitmapReceived(response);
//                    }
//                },
//                0,
//                0,
//                ImageView.ScaleType.CENTER_CROP,
//                Bitmap.Config.RGB_565,
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }
//        );
//        requestQueue.add(imageRequest);
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
