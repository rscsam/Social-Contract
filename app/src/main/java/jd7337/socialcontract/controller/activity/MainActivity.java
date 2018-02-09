package jd7337.socialcontract.controller.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.UpdateProfileFragment;
import jd7337.socialcontract.view.dialog.AuthenticationDialog;
import jd7337.socialcontract.controller.fragment.AccountManagementFragment;
import jd7337.socialcontract.controller.fragment.AccountSelectFragment;
import jd7337.socialcontract.controller.fragment.ConfirmPurchaseDialogFragment;
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
        ProfileFragment.ProfileFListener,
        AccountSelectFragment.AccountSelectFListener,
        ConfirmPurchaseDialogFragment.ConfirmPurchaseDialogFListener, UpdateProfileFragment.UpdateProfileFListener {

    private HomeFragment homeFragment;
    private UpdateProfileFragment updateProfileFragment;
    private DiscoverSettingsFragment discoverSettingsFragment;
    private DiscoverFragment discoverFragment;
    private GrowFragment growFragment;
    private EditInterestProfileFragment editInterestProfileFragment;
    private AccountManagementFragment accountManagementFragment;
    private ProfileFragment profileFragment;
    private AccountSelectFragment accountSelectFragment;
    private ConfirmPurchaseDialogFragment confirmPurchaseDialogFragment;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;

    private AuthenticationDialog auth_dialog;
    private Button btn_get_access_token;

    private int numCoins;
    private String email;
    private String userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("userId");
        email = getIntent().getStringExtra("email");

        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("userId", userId);

        // set the home fragment
        homeFragment = new HomeFragment();
        discoverSettingsFragment = new DiscoverSettingsFragment();
        discoverFragment = new DiscoverFragment();
        updateProfileFragment = new UpdateProfileFragment();
        growFragment = new GrowFragment();
        editInterestProfileFragment = new EditInterestProfileFragment();
        accountManagementFragment = new AccountManagementFragment();
        profileFragment = ProfileFragment.newInstance(bundle);
        accountSelectFragment = new AccountSelectFragment();
        confirmPurchaseDialogFragment = new ConfirmPurchaseDialogFragment();


        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_view, homeFragment).commit();

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

        // model initialization
        updateCoinNumber(20);

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

    // Navigates to the screen selected in the navigation drawer
    public void onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                showFragment(R.id.main_activity_view, homeFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_profile:
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                showFragmentWithBundle(R.id.main_activity_view, profileFragment, bundle);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_accounts:
                showFragment(R.id.main_activity_view, accountManagementFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_interests:
                showFragment(R.id.main_activity_view, editInterestProfileFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_discover:
                showFragment(R.id.main_activity_view, discoverSettingsFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_grow:
                showFragment(R.id.main_activity_view, accountSelectFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case R.id.nav_logout:
                mDrawerLayout.closeDrawer(mDrawerList);
                onLogout();
            default:
                mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

    @Override
    public void onClickGrowPurchase(int quantity, String type, int individualPrice) {
        confirmPurchaseDialogFragment.setQuantity(quantity);
        confirmPurchaseDialogFragment.setType(type);
        confirmPurchaseDialogFragment.setTotalPrice(quantity * individualPrice);
        confirmPurchaseDialogFragment.show(this.getFragmentManager(), "confirm_purchase_dialog");
    }

    @Override
    public void onClickDiscoverImDone() {
        showFragment(R.id.main_activity_view, homeFragment);
    }

    @Override
    public void onClickDiscoverSettingsGo() {
        showFragment(R.id.main_activity_view, discoverFragment);
    }

    @Override
    public void onClickHomeDiscover() {
        showFragment(R.id.main_activity_view, discoverSettingsFragment);
    }

    @Override
    public void onClickHomeGrow() {
        showFragment(R.id.main_activity_view, accountSelectFragment);
    }

    @Override
    public void onClickEIPSubmit() {showFragment(R.id.main_activity_view, homeFragment);}

    @Override
    public void onClickAccountManagement() {
        showFragment(R.id.main_activity_view, accountManagementFragment);
    }

    @Override
    public void onClickChangePasswordTwo() {
        showFragment(R.id.main_activity_view, updateProfileFragment);
    }

    @Override
    public void onClickInterestProfile() {showFragment(R.id.main_activity_view, editInterestProfileFragment);}

    /**
     * Profile fragment
     * Allows editing email.
     */
    public void onClickChangeEmail(View view) {
        Toast.makeText(this, "toggle called", Toast.LENGTH_SHORT).show();
        Button changeEmailButton = (Button) view.findViewById(R.id.change_email_bt);
        System.out.println(changeEmailButton);
        EditText editEmailEditText = (EditText) view.findViewById(R.id.edit_email_et);
        System.out.println(editEmailEditText);
        changeEmailButton.setVisibility(View.GONE);
        editEmailEditText.setVisibility(View.VISIBLE);
    }

    /**
     * Profile fragment
     * Stops editing email
     */
    public void onClickConfirmEmail(View view) {
        Button changeEmailButton = (Button) view.findViewById(R.id.change_email_bt);
        EditText editEmailEditText = (EditText) view.findViewById(R.id.edit_email_et);
        changeEmailButton.setVisibility(View.VISIBLE);
        editEmailEditText.setVisibility(View.GONE);
    }

    /**
     * Profile fragment
     * Allows editing password.
     */
    public void onClickChangePassword(View view) {
        Toast.makeText(this, "toggle called", Toast.LENGTH_SHORT).show();
        Button changePasswordButton = (Button) view.findViewById(R.id.change_password_bt);
        System.out.println(changePasswordButton);
        EditText editPasswordEditText = (EditText) view.findViewById(R.id.edit_password_et);
        System.out.println(editPasswordEditText);
        TextView noRequestTextView = (TextView) view.findViewById(R.id.home_no_request_tv);
        System.out.println(noRequestTextView);
        Button accountManagementButton = (Button) view.findViewById(R.id.account_management_button);
        System.out.println(accountManagementButton);
        Button changeEmailButton = (Button) view.findViewById(R.id.change_email_bt);
        System.out.println(changeEmailButton);
        changePasswordButton.setVisibility(View.GONE);
        editPasswordEditText.setVisibility(View.VISIBLE);
    }

    /**
     * Profile fragment
     * Stops editing password
     */
    public void onClickConfirmPassword(View view) {
        Button changePasswordButton = (Button) view.findViewById(R.id.change_password_bt);
        EditText editPasswordEditText = (EditText) view.findViewById(R.id.edit_password_et);
        changePasswordButton.setVisibility(View.GONE);
        editPasswordEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickAccount() {showFragment(R.id.main_activity_view, growFragment);}

    @Override
    public void onClickConfirmPurchase(int totalCoins) {
        updateCoinNumber(numCoins - totalCoins);
        Bundle bundle = new Bundle();
        bundle.putString("request", "1");
        showFragmentWithBundle(R.id.main_activity_view, homeFragment, bundle);
    }

    // goes to the profile screen
    public void onClickProfile(View view) {
        showFragment(R.id.main_activity_view, profileFragment);
    }

    // logs out of the application
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

    private void showFragmentWithBundle(int viewId, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateCoinNumber(int newNumber) {
        numCoins = newNumber;
        TextView coinTV = findViewById(R.id.num_coins_tv);
        String newCoinNumStr = newNumber + "";
        coinTV.setText(newCoinNumStr);
    }
}
