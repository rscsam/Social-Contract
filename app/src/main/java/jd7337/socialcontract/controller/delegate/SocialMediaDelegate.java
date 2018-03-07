package jd7337.socialcontract.controller.delegate;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by sam on 2/24/18.
 */

public class SocialMediaDelegate {

    public void receiveBitmapFromInstagram(Context context, String accessToken, String mediaId,
                                           final OnBitmapReceivedListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://api.instagram.com/v1/media/" + mediaId + "?access_token=" + accessToken;
        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                url, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        listener.onBitmapReceived(response);
                    }
                },
                100,
                100,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(imageRequest);
    }

    public interface OnBitmapReceivedListener {
        void onBitmapReceived(Bitmap response);
    }
}
