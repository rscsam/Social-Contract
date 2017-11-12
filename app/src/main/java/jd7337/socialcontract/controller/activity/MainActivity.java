package jd7337.socialcontract.controller.activity;

import android.accounts.Account;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.AccountManagementFragment;
import jd7337.socialcontract.controller.fragment.DiscoverFragment;
import jd7337.socialcontract.controller.fragment.DiscoverSettingsFragment;
import jd7337.socialcontract.controller.fragment.EditInterestProfileFragment;
import jd7337.socialcontract.controller.fragment.GrowFragment;
import jd7337.socialcontract.controller.fragment.HomeFragment;
import jd7337.socialcontract.controller.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity implements
    HomeFragment.HomeFListener, DiscoverSettingsFragment.DiscoverSettingsFListener,
    DiscoverFragment.DiscoverFListener, GrowFragment.GrowFListener,
        EditInterestProfileFragment.EditInterestProfileFListener,
        AccountManagementFragment.AccountManagementFListener,
        ProfileFragment.ProfileFListener{

    private HomeFragment homeFragment;
    private DiscoverSettingsFragment discoverSettingsFragment;
    private DiscoverFragment discoverFragment;
    private GrowFragment growFragment;
    private EditInterestProfileFragment editInterestProfileFragment;
    private AccountManagementFragment accountManagementFragment;
    private ProfileFragment profileFragment;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the home fragment
        homeFragment = new HomeFragment();
        discoverSettingsFragment = new DiscoverSettingsFragment();
        discoverFragment = new DiscoverFragment();
        growFragment = new GrowFragment();
        editInterestProfileFragment = new EditInterestProfileFragment();
        accountManagementFragment = new AccountManagementFragment();
        profileFragment = new ProfileFragment();


        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_layout, homeFragment).commit();

        // set the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (NavigationView) findViewById(R.id.navigation);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                showFragment(R.id.main_activity_layout, homeFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_profile:
                showFragment(R.id.main_activity_layout, profileFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_accounts:
                showFragment(R.id.main_activity_layout, accountManagementFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_interests:
                showFragment(R.id.main_activity_layout, editInterestProfileFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_discover:
                showFragment(R.id.main_activity_layout, discoverFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_grow:
                showFragment(R.id.main_activity_layout, growFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_logout:
                mDrawerLayout.closeDrawer(mDrawerList);
                onLogout();
            default:
                mDrawerLayout.closeDrawer(mDrawerList);
        }

    }
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

    @Override
    public void onClickEIPSubmit() {showFragmentNoBackStack(R.id.main_activity_layout, accountManagementFragment);}

    public void onLogout() {
        Intent startLogin = new Intent(this, LoginActivity.class);
        startLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLogin);
        finish();
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
