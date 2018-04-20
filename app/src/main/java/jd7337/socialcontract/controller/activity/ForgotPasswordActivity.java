package jd7337.socialcontract.controller.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.GMailSender;

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

    public void onClickSend(View view) throws JSONException {
        email = emailET.getText().toString();
        setDefaultPassword();
        finish();

    }

    /**
     * Connect to the change password endpoint on the server to change the user's password in the database
     * Hashes the password first
     */
    private void setDefaultPassword() throws JSONException {

        String url = ServerDelegate.SERVER_URL + "/loginInit";
        final Context mContext = this;
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", email);
        ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    salt = response.getString("salt");
                    nonce = response.getString("nonce");
                    userId = response.getString("userId");
                    newPass = new String(randomPassword(8));
                    hashPassword(newPass, salt);
                } else {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * Connect to the change password endpoint on the server to change the user's password in the database
     * Hashes the password first
     * @param hashedPassword - hashed password to change to
     */
    public void changePassword(String hashedPassword) {
        String url = ServerDelegate.SERVER_URL + "/changePassword";
        Map<String, String> params = new HashMap<>();
        params.put("password", hashedPassword);
        params.put("userId", userId);
        ServerDelegate.postRequest(getApplicationContext(), url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    Toast.makeText(getApplicationContext(),
                            "Successfully changed password", Toast.LENGTH_SHORT).show();
                    sentEmail();
                } else {
                    Toast.makeText(getApplicationContext(),
                            response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sentEmail() {
        final String emailBody = "Your new password is: " + newPass;
        Log.i("debug", newPass);
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
     * Uses the salt to hash the password, and then calls change password
     * @param password - password to be hashed
     *
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



}