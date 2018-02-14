package jd7337.socialcontract.controller.delegate;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.InterestProfile;

/**
 * Created by sam on 2/11/18.
 */
public class ServerDelegate {

    public static void sendInterestProfile(final Context context, String socialContractId, InterestProfile interestProfile) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/updateInterestProfile";

        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("music", interestProfile.getMusic());
            requestParams.put("sports", interestProfile.getSports());
            requestParams.put("movies", interestProfile.getMovies());
            requestParams.put("videogames", interestProfile.getVideoGames());
            requestParams.put("music", interestProfile.getFood());
            requestParams.put("memes", interestProfile.getMemes());
            requestParams.put("socialContractId", socialContractId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                        } catch (JSONException e) {
                            Toast.makeText(context, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
