package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jd7337.socialcontract.R;

public class ConfirmEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_confirmemail);

    }

    public void onClickConfirm(View view) {
        Intent startTutorial = new Intent(this, Tutorial2Activity.class);
        startActivity(startTutorial);
    }
}
