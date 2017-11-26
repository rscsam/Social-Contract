package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
    }

    public void onClickForgetPassword(View view) {
        Intent starResetPassword = new Intent(this, ForgotPasswordActivity.class);
        startActivity(starResetPassword);
    }
}
