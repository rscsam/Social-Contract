package jd7337.socialcontract.controller.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.EditInterestProfileFragment;
import jd7337.socialcontract.controller.fragment.EditInterestProfilePromptFragment;
import jd7337.socialcontract.controller.fragment.InitialConnectAccountFragment;

public class TutorialActivity extends AppCompatActivity
        implements InitialConnectAccountFragment.OnInitialConnectAccountInteractionListener,
        EditInterestProfilePromptFragment.OnFragmentInteractionListener {

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

        // add the first
        getSupportFragmentManager().beginTransaction().add(R.id.tutorial_activity,
                icaFragment).commit();
    }

    @Override
    public void onClickICAFConnectAccount() {
        // replace the current fragment with eippFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.tutorial_activity, eippFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
