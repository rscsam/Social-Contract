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

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mPasswordConfirmET;
    private Button mRegisterButton;

    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmailET = findViewById(R.id.register_email_et);
        mPasswordET = findViewById(R.id.register_password_et);
        mPasswordConfirmET = findViewById(R.id.register_password_confirm_et);
        mRegisterButton = findViewById(R.id.register_btn);
    }

    public void onClickRegisterDone(View view) {

        mRegisterButton.setEnabled(false);
        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();

        if (checkPasswords()) {
            if(isValidEmail(mEmail)) {
                getSalt();
            } else {
                Toast.makeText(RegistrationActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                mRegisterButton.setEnabled(true);
            }
        } else {
            mRegisterButton.setEnabled(true);
        }
    }

    private void launchTutorial() {
        Intent startConfirmEmail = new Intent(this, Tutorial2Activity.class);
        startActivity(startConfirmEmail);
    }

    private void getSalt() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/initRegistration";


        Map<String, String> params = new HashMap<>();
        params.put("email", mEmail);


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                register(response.getString("result"));
                            } else {
                                Toast.makeText(RegistrationActivity.this, response.getString("result"), Toast.LENGTH_SHORT).show();
                                mRegisterButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            mRegisterButton.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        queue.add(stringRequest);
    }

    private void register(String salt) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/register";



        Map<String, String> params = new HashMap<>();
        params.put("email", mEmail);
        params.put("password", hash(salt));
        params.put("salt", salt);


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                mRegisterButton.setEnabled(true);
                                launchTutorial();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                mRegisterButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            mRegisterButton.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        queue.add(stringRequest);
    }

    private String hash(String salt) {
        String concat = mPassword + salt;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(concat.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //this is impossible
            return concat.substring(0,60);
        }
    }

    private boolean checkPasswords() {
        String passwordETText = mPasswordET.getText().toString();
        String confirmPasswordText = mPasswordConfirmET.getText().toString();

        if (passwordETText.isEmpty() || confirmPasswordText.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(passwordETText.equals(confirmPasswordText)) {
            return true;
        } else {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
