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

    private EditText emailET;
    private EditText passwordET;
    private EditText passwordConfirmET;
    private Button registerButton;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailET = findViewById(R.id.register_email_et);
        passwordET = findViewById(R.id.register_password_et);
        passwordConfirmET = findViewById(R.id.register_password_confirm_et);
        registerButton = findViewById(R.id.register_btn);
    }

    /**
     * Attempts to register the user
     * @param view the view
     */
    public void onClickRegisterDone(View view) {

        // turn off register button while registration is occuring
        registerButton.setEnabled(false);
        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        // if passwords and email are valid, gets a salt from the server
        if (checkPasswords()) {
            if(isValidEmail(email)) {
                getSalt();
            } else {
                Toast.makeText(RegistrationActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        } else {
            registerButton.setEnabled(true);
        }
    }

    /**
     * Starts tutorial activity
     */
    private void launchTutorial() {
        Intent startConfirmEmail = new Intent(this, TutorialActivity.class);
        startActivity(startConfirmEmail);
    }

    /**
     * Starts a volley request for getting a salt from the database.
     * Launches register() if it succeeds
     */
    private void getSalt() {
        // create a requestqueue to start volley requests
        RequestQueue queue = Volley.newRequestQueue(this);
        // use the url of the endpoint you are trying to connect to
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/initRegistration";

        // put all the parameters in a map
        // these will be converted into a JSON object and passed to the server
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        // the volley request
        // be sure to use POST or GET as necessary
        // onResponse and onErrorResponse are the callbacks: get called after the async request finishes
        // needs to override getHeaders or the server will not accept it
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
                                registerButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                            registerButton.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
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

        // adding the request to the queue is all you have to do to start it
        queue.add(stringRequest);
    }

    /**
     * Registers the user in the database
     * @param salt the salt returned from the database
     */
    private void register(String salt) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/register";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", hash(salt));
        params.put("salt", salt);


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                launchTutorial();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                        registerButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
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

    /**
     * hashes the user's inputted password with the salt from the server
     * Use SHA-256 on server and client
     * @param salt the salt from the server
     * @return hash(password and salt)
     */
    private String hash(String salt) {
        String concat = password + salt;
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

    /**
     * Ensures passwords are valid (match and are non-empty)
     * @return true if passwords are valid
     */
    private boolean checkPasswords() {
        String passwordETText = passwordET.getText().toString();
        String confirmPasswordText = passwordConfirmET.getText().toString();

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

    /**
     * checks if email is valid
     * @param target email to be checked
     * @return true if email is valid
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
