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
import jd7337.socialcontract.controller.activity.LoginActivity;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;

    private Context mContext;

    private String userId;

    public AccountManagementFragment() {
        // Required empty public constructor
    }

    public static AccountManagementFragment newInstance(String userId) {
        AccountManagementFragment fragment = new AccountManagementFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        mContext = getActivity();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Deletes a user's Twitter account
     */
    private void deleteTwitterAccount(){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/deleteTwitter";

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userId);

        String twitterId = "test";
        params.put("twitterId", twitterId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(getActivity(), "Twitter account deleted", Toast.LENGTH_LONG).show();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Cannot contact server", Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void deleteFacebookAccount(){}
    private void deleteInstagamAccount(){}

    public interface AccountManagementFListener {
    }
}
