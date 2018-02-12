package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private String password;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // grab user's email from the intent
        email = getArguments().getString("email");
        userId = getArguments().getString("userId");
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
        // Set the user's email to display
        TextView emailTextView = (TextView) view.findViewById(R.id.displayed_email_tv);
        emailTextView.setText(email);
        // Button to start changing user's email
        Button changeEmailButton = (Button) view.findViewById(R.id.change_email_bt);
        changeEmailButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editEmailEditText = (EditText) container.findViewById(R.id.edit_email_et);
                        Button confirmEmailButton = (Button) container.findViewById(R.id.confirm_email_bt);
                        Button cancelEmailButton = (Button) container.findViewById(R.id.cancel_email_bt);
                        view.setVisibility(View.GONE);
                        editEmailEditText.setVisibility(View.VISIBLE);
                        confirmEmailButton.setVisibility(View.VISIBLE);
                        cancelEmailButton.setVisibility(View.VISIBLE);
                    }
                }
        );
        // Submit change to user's email
        Button confirmEmailButton = (Button) view.findViewById(R.id.confirm_email_bt);
        confirmEmailButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editEmailEditText = (EditText) container.findViewById(R.id.edit_email_et);
                        String newEmail = editEmailEditText.getText().toString();
                        if (isValidEmail(newEmail)) {
                            changeEmail(newEmail);
                        } else {
                            Toast.makeText(getContext(), "Invalid email format.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Button cancelEmailButton = (Button) view.findViewById(R.id.cancel_email_bt);
        cancelEmailButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editEmailEditText = (EditText) container.findViewById(R.id.edit_email_et);
                        Button confirmEmailButton = (Button) container.findViewById(R.id.confirm_email_bt);
                        Button changeEmailButton = (Button) container.findViewById(R.id.change_email_bt);
                        view.setVisibility(View.GONE);
                        editEmailEditText.setVisibility(View.GONE);
                        confirmEmailButton.setVisibility(View.GONE);
                        changeEmailButton.setVisibility(View.VISIBLE);
                    }
                }
        );
        // Allow user to begin editing password
        Button changePasswordButton = (Button) view.findViewById(R.id.change_password_bt);
        changePasswordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editPasswordEditText = (EditText) container.findViewById(R.id.edit_password_et);
                        Button confirmPasswordButton = (Button) container.findViewById(R.id.confirm_password_bt);
                        Button cancelPasswordButton = (Button) container.findViewById(R.id.cancel_password_bt);
                        view.setVisibility(View.GONE);
                        editPasswordEditText.setVisibility(View.VISIBLE);
                        confirmPasswordButton.setVisibility(View.VISIBLE);
                        cancelPasswordButton.setVisibility(View.VISIBLE);
                    }
                }
        );
        // Submit change to password
        Button confirmPasswordButton = (Button) view.findViewById(R.id.confirm_password_bt);
        confirmPasswordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editPasswordEditText = container.findViewById(R.id.edit_password_et);
                        String newPassword = editPasswordEditText.getText().toString();
                        if (!newPassword.isEmpty()) {
                            password = newPassword;
                            initChangePassword();
                        } else {
                            Toast.makeText(getContext(), "Password can't be blank.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        // Stop editing password and don't change
        Button cancelPasswordButton = (Button) view.findViewById(R.id.cancel_password_bt);
        cancelPasswordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editPasswordEditText = (EditText) container.findViewById(R.id.edit_password_et);
                        Button confirmPasswordButton = (Button) container.findViewById(R.id.confirm_password_bt);
                        Button changePasswordButton = (Button) container.findViewById(R.id.change_password_bt);
                        view.setVisibility(View.GONE);
                        editPasswordEditText.setVisibility(View.GONE);
                        confirmPasswordButton.setVisibility(View.GONE);
                        changePasswordButton.setVisibility(View.VISIBLE);
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
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
    /**
     * Connect to the change password endpoint on the server to change the user's password in the database
     * Hashes the password first
     * @param hashedPassword - hashed password to change to
     */
    public void changePassword(String hashedPassword) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changePassword";

        System.out.println(hashedPassword);

        Map<String, String> params = new HashMap<>();
        params.put("password", hashedPassword);
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
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        System.out.println(jsonObjectRequest);

        queue.add(jsonObjectRequest);
    }

    /**
     * Starts the change password sequence.
     * Connects to loginInit endpoint to get user's salt.
     * Then calls hash password with the salt.
     */
    private void initChangePassword() {
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
                                hashPassword(password, response.getString("salt"));
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

        queue.add(jsonObjectRequest);
    }

    /**
     * checks if email is in a valid format
     * @param checkedEmail - email to be checked
     * @return boolean - true if email is in a valid format
     */
    private boolean isValidEmail(String checkedEmail) {
        return !TextUtils.isEmpty(checkedEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(checkedEmail).matches();
    }

    /**
     * Uses the salt to hash the password, and then calls change password
     * @param password - password to be hashed
     * @return String - the hashed password
     */
    private void hashPassword(String password, String salt) {
        String concat = password + salt;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(concat.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            changePassword(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            changePassword(concat.substring(0,60));
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
