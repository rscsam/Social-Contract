package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;

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
        String url = ServerDelegate.SERVER_URL + "/loginInit";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        ServerDelegate.postRequest(this, url, params,
            new ServerDelegate.OnResultListener() {
                @Override
                public void onResult(boolean success, JSONObject response) throws JSONException {
                    if (success) {
                        login(response.getString("salt"), response.getString("nonce"));
                        userId = response.getString("userId");
                    } else {
                        Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                    }
                }
            }, new ServerDelegate.OnJSONErrorListener() {
                @Override
                public void onJSONError(JSONException e) {
                    Toast.makeText(LoginActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                }
            }, new ServerDelegate.OnErrorListener() {
                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                }
            });
    }

    /**
     * Calls login endpoint
     * @param salt the salt to pass to hashpassword
     * @param nonce the nonce to pass to hashpassword
     */
    private void login(String salt, String nonce) {
        String pass = hashPassword(salt, nonce);

        String url = ServerDelegate.SERVER_URL + "/login";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pass);
        ServerDelegate.postRequest(this, url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
                loginButton.setEnabled(true);
            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(LoginActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
            }
        });
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
