package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jd7337.socialcontract.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void onClickRegister(View view) {
        Intent startTutorial = new Intent(this, TutorialActivity.class);
        startActivity(startTutorial);
    }
}
