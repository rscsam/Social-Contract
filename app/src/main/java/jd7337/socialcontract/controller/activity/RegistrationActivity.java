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
     * @param id the id of the user
     */
    private void launchTutorial(String id) {
        Intent startTutorial = new Intent(this, TutorialActivity.class);
        startTutorial.putExtra("userId", id);
        startTutorial.putExtra("email", email);
        startActivity(startTutorial);
    }

    /**
     * Starts a volley request for getting a salt from the database.
     * Launches register() if it succeeds
     */
    private void getSalt() {
        String url = ServerDelegate.SERVER_URL + "/initRegistration";

        // put all the parameters in a map
        // these will be converted into a JSON object and passed to the server
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        ServerDelegate.postRequest(this, url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    register(response.getString("result"));
                } else {
                    Toast.makeText(RegistrationActivity.this, response.getString("result"), Toast.LENGTH_SHORT).show();
                    registerButton.setEnabled(true);
                }
            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        });
    }

    /**
     * Registers the user in the database
     * @param salt the salt returned from the database
     */
    private void register(String salt) {
        String url = ServerDelegate.SERVER_URL + "/register";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", hash(salt));
        params.put("salt", salt);
        ServerDelegate.postRequest(this, url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    launchTutorial(response.getString("userId"));
                } else {
                    Toast.makeText(RegistrationActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
                registerButton.setEnabled(true);
            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(RegistrationActivity.this, "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        });
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
