package jd7337.socialcontract.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Random;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.ProfileFragment;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailET;
    private String email;
    private String userId;
    private String salt;
    private String nonce;
    private String newPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailET = findViewById(R.id.forgot_email);
    }

    public void onClickSend(View view) {

        email = emailET.getText().toString();
        Log.i("debug", "reached here");
        setDefaultPassword();
        final String emailBody = "Your new password is: " + newPass;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("socialContractTest@gmail.com", "TheRipGetRipper");
                    sender.sendMail("Password Reset Request",
                            emailBody,
                            "socialContractTest@gmail.com", email);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();


    }

    /**
     * Connect to the change password endpoint on the server to change the user's password in the database
     * Hashes the password first
     */
    private void setDefaultPassword() {

        //ensures the salt and nonce have been retrieved
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/loginInit";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        final Context thisContext = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String hashedPassword;
                                salt = response.getString("salt");
                                nonce = response.getString("nonce");
                                userId = response.getString("userId");
                                newPass = new String(randomPassword(8));
                                hashedPassword = hashPassword(newPass);
                                Log.i("debug", hashedPassword);
                                RequestQueue queue = Volley.newRequestQueue(thisContext);
                                String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/changePassword";

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
                                                        Toast.makeText(getApplicationContext(),
                                                                "Successfully changed password", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),
                                                                response.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(),
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
                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);






    }

    /**
     * Connects to loginInit endpoint to get user's salt and nonce.
     */
    private boolean checkUser() {
        final boolean[] userExist = {false};
        RequestQueue queue = Volley.newRequestQueue(this);
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
                                userExist[0] = true;
                                salt = response.getString("salt");
                                nonce = response.getString("nonce");
                                userId = response.getString("userId");
                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return userExist[0];
    }


    private static char[] randomPassword(int len) {

        // A strong password has Cap_chars, Lower_chars,
        // numeric value and symbols. So we are using all of
        // them to generate our password
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*_=+-/.?<>)";

        String values = Capital_chars + Small_chars +
                numbers + symbols;

        // Using random method
        Random rndm_method = new Random();

        char[] password = new char[len];

        for (int i = 0; i < len; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            password[i] =
                    values.charAt(rndm_method.nextInt(values.length()));

        }
        return password;
    }

    /**
     * Retrieves the salt and nonce and uses them to hash the password
     * @param password - password to be hashed
     * @return String - the hashed password
     */
    private String hashPassword(String password) {
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

}
