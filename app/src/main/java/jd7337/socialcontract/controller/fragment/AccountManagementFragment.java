package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import jd7337.socialcontract.R;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;

    public AccountManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_management, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountManagementFListener) {
            mListener = (AccountManagementFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AccountManagementFListener");
        }
    }

    /**
     * Connect to the change email endpoint on the server to change the user's email in the database
     */
    public void changeEmail(String oldEmail, String newEmail) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changeEmail";

        Map<String, String> params = new HashMap<>();
        params.put("oldEmail", oldEmail);
        params.put("newEmail", newEmail);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(AccountManagementFragment.super.getContext(),
                                        "Successfully changed email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountManagementFragment.super.getContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AccountManagementFragment.super.getContext(),
                                    "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AccountManagementFragment.super.getContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application.json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
    /**
     * Connect to the change password endpoint on the server to change the user's password in the database
     */
    public void changePassword(String oldEmail, String newPassword) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changePassword";

        Map<String, String> params = new HashMap<>();
        params.put("oldEmail", oldEmail);
        params.put("newEmail", newPassword);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(AccountManagementFragment.super.getContext(),
                                        "Successfully changed password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountManagementFragment.super.getContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AccountManagementFragment.super.getContext(),
                                    "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AccountManagementFragment.super.getContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application.json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountManagementFListener {
    }
}
