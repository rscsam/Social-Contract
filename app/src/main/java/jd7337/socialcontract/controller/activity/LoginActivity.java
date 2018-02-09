package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import jd7337.socialcontract.R;

/**
 * Activity that lets the user log in, or go to registration and forgot password page
 */
public class LoginActivity extends AppCompatActivity {

    private EditText passwordET;
    private EditText emailET;
    private String email;
    private String password;
    private Button loginButton;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Launches registration activity
     * @param view the view
     */
    public void onClickRegister(View view) {
        Intent startRegistration = new Intent(this, RegistrationActivity.class);
        startActivity(startRegistration);
    }

    /**
     * Launches forgot password activity
     * @param view the view
     */
    public void onClickForgetPassword(View view) {
        Intent startResetPassword = new Intent(this, ForgotPasswordActivity.class);
        startActivity(startResetPassword);
    }

    /**
     * Launches main activity
     */
    private void startMainActivity() {
        Intent startMain = new Intent(this, MainActivity.class);
        startMain.putExtra("email", email);
        startMain.putExtra("userId", userId);
        startMain.putExtra("email", email);
        startActivity(startMain);
    }

    /**
     * Starts initLogin if password and email are valid
     * @param view the view
     */
    public void onClickLogin(View view) {

        emailET = findViewById(R.id.login_email_et);
        passwordET = findViewById(R.id.login_password_et);

        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        loginButton = findViewById(R.id.login_login_button);

        loginButton.setEnabled(false);

        if(checkPassword() & isValidEmail(email)) {
            initLogin();
        } else {
            loginButton.setEnabled(true);
        }
    }

    /**
     * Connects to loginInit endpoint. Gets nonce and salt if user exists
     * If successful, starts login
     */
    public void initLogin() {
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
                                login(response.getString("salt"), response.getString("nonce"));
                                userId = response.getString("userId");
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                loginButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
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

    /**
     * Calls login endpoint
     * @param salt the salt to pass to hashpassword
     * @param nonce the nonce to pass to hashpassword
     */
    private void login(String salt, String nonce) {
        String pass = hashPassword(salt, nonce);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/login";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pass);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                startMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                        loginButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
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

    /**
     * Called after initLogin. Hashes the password
     * @param salt the salt to hash the password with
     * @param nonce the nonce to hash the password with
     * @return String the hashed password
     */
    private String hashPassword(String salt, String nonce) {
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
            //this is impossible
            return concat.substring(0,60);
        }
    }

    /**
     * Ensures password is non-empty
     * @return true if passwords are valid
     */
    private boolean checkPassword() {
        String passwordETText = passwordET.getText().toString();

        if (passwordETText.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * checks if email is valid
     * @param target email to be checked
     * @return true if email is valid
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }




}
