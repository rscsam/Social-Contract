package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.EditInterestProfileFragment;
import jd7337.socialcontract.controller.fragment.EditInterestProfilePromptFragment;
import jd7337.socialcontract.controller.fragment.InitialConnectAccountFragment;


public class TutorialActivity extends AppCompatActivity
        implements InitialConnectAccountFragment.InitialConnectAccountFListener,
        EditInterestProfilePromptFragment.EditInterestProfilePromptFListener,
        EditInterestProfileFragment.EditInterestProfileFListener {

    private InitialConnectAccountFragment icaFragment;
    private EditInterestProfilePromptFragment eippFragment;
    private EditInterestProfileFragment eipFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if (savedInstanceState != null) {
            return;
        }

        // init fragments in code
        icaFragment = new InitialConnectAccountFragment();
        eippFragment = new EditInterestProfilePromptFragment();
        eipFragment = new EditInterestProfileFragment();

        // show initial fragment
        getSupportFragmentManager().beginTransaction().add(R.id.tutorial_activity,
                icaFragment).commit();
    }

    @Override
    public void onClickICAFConnectAccount() {
        showFragment(R.id.tutorial_activity, eippFragment);
    }

    @Override
    public void onClickEIPPEditProfile() {
        showFragment(R.id.tutorial_activity, eipFragment);
    }

    @Override
    public void onClickEIPPSkip() {
        startMainActivity();
    }

    @Override
    public void onClickEIPSubmit() {
        startMainActivity();
    }

    private void startMainActivity() {
        Intent startMain = new Intent(this, MainActivity.class);
        startActivity(startMain);
    }

    private void showFragment(int viewId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showFragmentNoBackStack(int viewId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, fragment);
        transaction.commit();
    }
}
