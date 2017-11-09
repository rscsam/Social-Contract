package jd7337.socialcontract.controller.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.AccountManagementFragment;
import jd7337.socialcontract.controller.fragment.DiscoverFragment;
import jd7337.socialcontract.controller.fragment.DiscoverSettingsFragment;
import jd7337.socialcontract.controller.fragment.GrowFragment;
import jd7337.socialcontract.controller.fragment.HomeFragment;
import jd7337.socialcontract.controller.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity implements
    HomeFragment.HomeFListener, DiscoverSettingsFragment.DiscoverSettingsFListener,
    DiscoverFragment.DiscoverFListener, GrowFragment.GrowFListener {

    private HomeFragment homeFragment;
    private DiscoverSettingsFragment discoverSettingsFragment;
    private DiscoverFragment discoverFragment;
    private GrowFragment growFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        discoverSettingsFragment = new DiscoverSettingsFragment();
        discoverFragment = new DiscoverFragment();
        growFragment = new GrowFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_layout, homeFragment).commit();
    }

    @Override
    public void onClickGrowPurchase() {
        showFragment(R.id.main_activity_layout, homeFragment);
    }

    @Override
    public void onClickDiscoverImDone() {
        showFragment(R.id.main_activity_layout, homeFragment);
    }

    @Override
    public void onClickDiscoverSettingsGo() {
        showFragment(R.id.main_activity_layout, discoverFragment);
    }

    @Override
    public void onClickHomeDiscover() {
        showFragment(R.id.main_activity_layout, discoverSettingsFragment);
    }

    @Override
    public void onClickHomeGrow() {
        showFragment(R.id.main_activity_layout, growFragment);
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
