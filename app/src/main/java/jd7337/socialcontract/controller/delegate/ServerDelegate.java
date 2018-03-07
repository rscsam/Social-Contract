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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.model.InterestProfile;

/**
 * Created by sam on 2/11/18.
 *
 */
public class ServerDelegate {

    public static final String SERVER_URL = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000";

    // Provide null for any listener if you don't care what happens after the request is processed
    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success;
                            try {
                                success = response.getBoolean("success");
                            } catch (JSONException e) {
                                success = response != null;
                            }
                            if (null != resultListener) {
                                resultListener.onResult(success, response);
                            }
                            } catch (JSONException e) {
                            if (null != jsonErrorListener) {
                                jsonErrorListener.onJSONError(e);
                            } else {
                                Toast.makeText(context, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != errorListener) {
                    errorListener.onError(error);
                } else {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
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

    public static void postRequest(final Context context, String url) {
        postRequest(context, url, new JSONObject(), null, null, null);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams) {
        postRequest(context, url, requestParams, null, null, null);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams) {
        postRequest(context, url, new JSONObject(requestParams), null, null, null);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnResultListener resultListener) {
        postRequest(context, url, requestParams, resultListener, null, null);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnResultListener resultListener) {
        postRequest(context, url, new JSONObject(requestParams), resultListener, null, null);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnJSONErrorListener jsonErrorListener) {
        postRequest(context, url, requestParams, null, jsonErrorListener, null);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnJSONErrorListener jsonErrorListener) {
        postRequest(context, url, new JSONObject(requestParams), null, jsonErrorListener, null);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, requestParams, null, null, errorListener);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, new JSONObject(requestParams), null, null, errorListener);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener) {
        postRequest(context, url, requestParams, resultListener, jsonErrorListener, null);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener) {
        postRequest(context, url, new JSONObject(requestParams), resultListener, jsonErrorListener, null);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnResultListener resultListener,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, requestParams, resultListener, null, errorListener);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnResultListener resultListener,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, new JSONObject(requestParams), resultListener, null, errorListener);
    }

    public static void postRequest(final Context context, String url, JSONObject requestParams,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, requestParams, null, jsonErrorListener, errorListener);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, new JSONObject(requestParams), null, jsonErrorListener, errorListener);
    }

    public static void postRequest(final Context context, String url, Map<String, String> requestParams,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        postRequest(context, url, new JSONObject(requestParams), resultListener, jsonErrorListener, errorListener);
    }


    // Provide null for any listener if you don't care what happens after the request is processed
    public static void getRequest(final Context context, String url,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success;
                            try {
                                success = response.getBoolean("success");
                            } catch (JSONException e) {
                                success = response != null;
                            }
                            if (success) {
                                if (null != resultListener) {
                                    resultListener.onResult(success, response);
                                }
                            }
                        } catch (JSONException e) {
                            if (null != jsonErrorListener) {
                                jsonErrorListener.onJSONError(e);
                            } else {
                                Toast.makeText(context, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != errorListener) {
                    errorListener.onError(error);
                } else {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
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

    public static void getRequest(final Context context, String url) {
        getRequest(context, url, null, null, null);
    }

    public static void getRequest(final Context context, String url,
                                   final OnResultListener resultListener) {
        getRequest(context, url, resultListener, null, null);
    }


    public static void getRequest(final Context context, String url,
                                   final OnJSONErrorListener jsonErrorListener) {
        getRequest(context, url,null, jsonErrorListener, null);
    }


    public static void getRequest(final Context context, String url,
                                   final OnErrorListener errorListener) {
        getRequest(context, url, null, null, errorListener);
    }

    public static void getRequest(final Context context, String url,
                                   final OnResultListener resultListener,
                                   final OnJSONErrorListener jsonErrorListener) {
        getRequest(context, url, resultListener, jsonErrorListener, null);
    }

    public static void getRequest(final Context context, String url,
                                   final OnResultListener resultListener,
                                   final OnErrorListener errorListener) {
        getRequest(context, url, resultListener, null, errorListener);
    }

    public static void getRequest(final Context context, String url,
                                   final OnJSONErrorListener jsonErrorListener,
                                   final OnErrorListener errorListener) {
        getRequest(context, url,null, jsonErrorListener, errorListener);
    }

    public static void sendInterestProfile(final Context context, String socialContractId,
                                           InterestProfile interestProfile) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("music", interestProfile.getMusic());
            requestParams.put("sports", interestProfile.getSports());
            requestParams.put("movies", interestProfile.getMovies());
            requestParams.put("videogames", interestProfile.getVideoGames());
            requestParams.put("food", interestProfile.getFood());
            requestParams.put("memes", interestProfile.getMemes());
            requestParams.put("socialContractId", socialContractId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postRequest(context, SERVER_URL + "/updateInterestProfile", requestParams);
    }

    public interface OnResultListener {
        void onResult(boolean success, JSONObject response) throws JSONException;
    }

    public interface OnJSONErrorListener {
        void onJSONError(JSONException e);
    }

    public interface OnErrorListener {
        void onError(VolleyError error);
    }
}
