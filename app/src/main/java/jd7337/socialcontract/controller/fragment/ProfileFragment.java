package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;

public class ProfileFragment extends Fragment {

    private ProfileFListener mListener;

    private String email;
    private String userId;
    private String salt;
    private String nonce;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(Bundle bundle) {

        Bundle args = bundle;

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button accountManagementButton = (Button) view.findViewById(R.id.account_management_button);
        accountManagementButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickAccountManagement();
                    }
                }
        );

        Button editInterestProfileButton = (Button) view.findViewById(R.id.edit_interest_profile_button);
        editInterestProfileButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickInterestProfile();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFListener) {
            mListener = (ProfileFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileFListener");
        }
    }
    /**
     * Connect to the change email endpoint on the server to change the user's email in the database
     * Assumes email's format has already been checked
     * @param newEmail - new email to change to
     */
    public void changeEmail(String newEmail) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changeEmail";

        Map<String, String> params = new HashMap<>();
        params.put("email", newEmail);
        params.put("userId", userId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(ProfileFragment.super.getContext(),
                                        "Successfully changed email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileFragment.super.getContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ProfileFragment.super.getContext(),
                                    "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileFragment.super.getContext(),
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
     * @param password - password to change to
     */
    public void changePassword(String password) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changePassword";

        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("userId", userId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(ProfileFragment.super.getContext(),
                                        "Successfully changed password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileFragment.super.getContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ProfileFragment.super.getContext(),
                                    "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileFragment.super.getContext(),
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
     * Connects to loginInit endpoint to get user's salt and nonce.
     */
    private void getSaltAndNonce() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/loginInit";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                salt = response.getString("salt");
                                nonce = response.getString("nonce");
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * checks if email is in a valid format
     * @param checkedEmail - email to be checked
     * @retrun boolean - true if email is in a valid format
     */
    private boolean isValidEmail(String checkedEmail) {
        return !TextUtils.isEmpty(checkedEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(checkedEmail).matches();
    }

    /**
     * Hashes the password
     * @param salt - the salt to hash with
     * @param nonce - the nonce to hash with
     * @param password - password to be hashed
     * @return String - the hashed password
     */
    private String hashPassword(String salt, String nonce, String password) {
        String concat = password + salt;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(concat.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            String nextHash = sb.toString() + nonce;
            MessageDigest md2 = MessageDigest.getInstance("SHA-256");
            md2.update(nextHash.getBytes());
            byte[] digest2 = md2.digest();
            StringBuilder sb2 = new StringBuilder();
            for (byte b : digest2) {
                sb2.append(String.format("%02x", b & 0xff));
            }
            return sb2.toString();
        } catch (NoSuchAlgorithmException e) {
            return concat.substring(0,60);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ProfileFListener {
        void onClickAccountManagement();
        void onClickInterestProfile();
    }

}
