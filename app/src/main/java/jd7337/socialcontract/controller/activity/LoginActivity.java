package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import jd7337.socialcontract.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onClickRegister(View view) {
        Intent startRegistration = new Intent(this, RegistrationActivity.class);
        startActivity(startRegistration);
    }

    public void onClickLogin(View view) {
        Intent startHome = new Intent(this, MainActivity.class);
        startActivity(startHome);

        EditText emailET = findViewById(R.id.login_email_et);
        EditText passwordET = findViewById(R.id.login_password_et);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/login";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        queue.add(stringRequest);
    }



    public void onClickForgetPassword(View view) {
        Intent starResetPassword = new Intent(this, ForgotPasswordActivity.class);
        startActivity(starResetPassword);
    }
}
