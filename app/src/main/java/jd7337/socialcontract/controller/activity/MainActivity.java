package jd7337.socialcontract.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Twitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.controller.fragment.InitialConnectAccountFragment;
import jd7337.socialcontract.controller.fragment.UpdateProfileFragment;
import jd7337.socialcontract.model.InstagramPost;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.model.Request;
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
        ConfirmPurchaseDialogFragment.ConfirmPurchaseDialogFListener,
        InitialConnectAccountFragment.InitialConnectAccountFListener {

    private HomeFragment homeFragment;
    private DiscoverSettingsFragment discoverSettingsFragment;
    private DiscoverFragment discoverFragment;
    private GrowFragment growFragment;
    private EditInterestProfileFragment editInterestProfileFragment;
    private AccountManagementFragment accountManagementFragment;
    private ProfileFragment profileFragment;
    private AccountSelectFragment accountSelectFragment;
    private ConfirmPurchaseDialogFragment confirmPurchaseDialogFragment;
    private InitialConnectAccountFragment initialConnectAccountFragment;
    private Fragment currFragment;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private Queue<Request> requests;

    private int numCoins;
    private String email;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Twitter kit
        Twitter.initialize(this);

        email = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("userId");

        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("userId", userId);

        // set the home fragment
        homeFragment = HomeFragment.newInstance(userId);
        discoverSettingsFragment = DiscoverSettingsFragment.newInstance(bundle);
        discoverFragment = new DiscoverFragment();
        growFragment = new GrowFragment();
        editInterestProfileFragment = new EditInterestProfileFragment();
        accountManagementFragment = AccountManagementFragment.newInstance(userId);
        profileFragment = ProfileFragment.newInstance(bundle);
        accountSelectFragment = AccountSelectFragment.newInstance(bundle);
        confirmPurchaseDialogFragment = new ConfirmPurchaseDialogFragment();
        initialConnectAccountFragment = new InitialConnectAccountFragment();
        requests = new LinkedList<>();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_view, homeFragment).commit();
        currFragment = homeFragment;

        // set the navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.navigation);
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


        Menu menu = mDrawerList.getMenu();
        menu.findItem(R.id.nav_email).setTitle(email);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateCoinNumber();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

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
                showFragment(R.id.main_activity_view, profileFragment);
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
    public void onClickGrowPurchase(final int quantity, final SocialMediaAccount account,
                                    final String type, final int individualPrice) {
        final Activity mContext = this;
        updateCoinNumber(new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                int coins = response.getInt("coins");
                if (coins >= quantity * individualPrice) {
                    confirmPurchaseDialogFragment.setQuantity(quantity);
                    confirmPurchaseDialogFragment.setType(type);
                    confirmPurchaseDialogFragment.setTotalPrice(quantity * individualPrice);
                    confirmPurchaseDialogFragment.setAccount(account);
                    confirmPurchaseDialogFragment.setAccessToken(account.getAccessToken());
                    confirmPurchaseDialogFragment.show(mContext.getFragmentManager(), "confirm_purchase_dialog");
                }  else {
                    Toast.makeText(mContext, "You only have " + coins + " coins",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClickDiscoverImDone() {
        showFragment(R.id.main_activity_view, homeFragment);
    }

    /**
     * Sends an int representing the social media platform and an array representing interaction types
     * @param socialMediaTypeOrdinal - ordinal of the social media platform based on the ordinal
     * @param selectedInteractions - byte array corresponding to the interaction types selected.
     *                             Sends a 1 for selected and 0 for unselected.
     */
    @Override
    public void onClickDiscoverSettingsGo(int socialMediaTypeOrdinal, byte[] selectedInteractions, Long twitterId) {
        Bundle bundle = new Bundle();
        bundle.putInt("SocialMediaTypeOrdinal", socialMediaTypeOrdinal);
        bundle.putByteArray("SelectedInteractions", selectedInteractions);
        bundle.putLong("twitterId", twitterId);
        discoverFragment = DiscoverFragment.newInstance(bundle);
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
    public void onClickAccountManagement() {
        showFragment(R.id.main_activity_view, accountManagementFragment);
    }

    @Override
    public void onClickInterestProfile() {showFragment(R.id.main_activity_view, editInterestProfileFragment);}

    @Override
    public void onClickAccountConnect() {
        showFragment(R.id.main_activity_view, initialConnectAccountFragment);
    }

    @Override
    public void onClickAccount(SocialMediaAccount account) {
        Bundle growBundle = new Bundle();
        growBundle.putInt("typeInt", account.getTypeResource().ordinal());
        growBundle.putString("username", account.getUsername());
        growBundle.putString("id", account.getId());
        growBundle.putString("accessToken", account.getAccessToken());
        growFragment = GrowFragment.newInstance(growBundle);
        showFragment(R.id.main_activity_view, growFragment);
    }

    @Override
    public void onClickConfirmPurchase(SocialMediaAccount account, String type, int quantity,
                                       int totalPrice) {
        SocialMediaAccount.AccountType accountType = account.getTypeResource();
        String username = account.getUsername();
        if (accountType == SocialMediaAccount.AccountType.TWITTER) {
            if (type.equals("Like") || type.equals("Retweet")) {
                Intent startTwitter = new Intent(MainActivity.this, TwitterTimelineActivity.class);
                startTwitter.putExtra("twitterId", account.getId());
                startTwitter.putExtra("goal", quantity);
                startTwitter.putExtra("username", username);
                startTwitter.putExtra("type", type);
                startTwitter.putExtra("cost", totalPrice);
                startActivityForResult(startTwitter, 420);
            } else {
                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("socialContractId", getSocialContractId());
                    requestParams.put("twitterId", account.getId());
                    requestParams.put("goal", quantity);
                    requestParams.put("type", type);
                    requestParams.put("cost", totalPrice);
                    requestParams.put("mediaId", account.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = ServerDelegate.SERVER_URL + "/addTwitterQueue";
                final Context mContext = this;
                ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
                    @Override
                    public void onResult(boolean success, JSONObject response) throws JSONException {
                        if (success) {
                            Toast.makeText(mContext, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Request r = new Request(confirmPurchaseDialogFragment.getQuantity(),
                        confirmPurchaseDialogFragment.getType());
                requests.add(r);
                Bundle bundle = new Bundle();
                bundle.putString("request", "1");
                updateCoinNumber();
                showFragmentWithBundle(R.id.main_activity_view, homeFragment, bundle);
            }
        } else if (accountType == SocialMediaAccount.AccountType.INSTAGRAM) {
            if (type.equals("Like")) {
                Intent startInstagram = new Intent(MainActivity.this, InstagramFeedActivity.class);
                startInstagram.putExtra("instagramId", account.getId());
                startInstagram.putExtra("accessToken", account.getAccessToken());
                startInstagram.putExtra("goal", quantity);
                startInstagram.putExtra("username", username);
                startInstagram.putExtra("type", type);
                startInstagram.putExtra("cost", totalPrice);
                startActivityForResult(startInstagram, 421);
            } else {
                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("socialContractId", getSocialContractId());
                    requestParams.put("instagramId", account.getId());
                    requestParams.put("goal", quantity);
                    requestParams.put("type", type);
                    requestParams.put("cost", totalPrice);
                    requestParams.put("mediaId", account.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = ServerDelegate.SERVER_URL + "/addInstagramQueue";
                final Context mContext = this;
                ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
                    @Override
                    public void onResult(boolean success, JSONObject response) throws JSONException {
                        if (success) {
                            Toast.makeText(mContext, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Request r = new Request(confirmPurchaseDialogFragment.getQuantity(),
                        confirmPurchaseDialogFragment.getType());
                requests.add(r);
                Bundle bundle = new Bundle();
                bundle.putString("request", "1");
                updateCoinNumber();
                showFragmentWithBundle(R.id.main_activity_view, homeFragment, bundle);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 420 && resultCode == -1) {  // Twitter feed returned
            Long tweetId = data.getLongExtra("tweetId", -1);
            String mediaId = tweetId.toString();
            String twitterId = data.getStringExtra("twitterId");
            int goal = data.getIntExtra("goal", -1);
            int cost = data.getIntExtra("cost", 0);
            String type = data.getStringExtra("type");
            JSONObject requestParams = new JSONObject();
            try {
                requestParams.put("socialContractId", getSocialContractId());
                requestParams.put("twitterId", twitterId);
                requestParams.put("mediaId", mediaId);
                requestParams.put("goal", goal);
                requestParams.put("type", type);
                requestParams.put("cost", cost);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = ServerDelegate.SERVER_URL + "/addTwitterQueue";
            final Context mContext = this;
            ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
                @Override
                public void onResult(boolean success, JSONObject response) throws JSONException {
                    if (success) {
                        Toast.makeText(mContext, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                        Request r = new Request(confirmPurchaseDialogFragment.getQuantity(),
                                confirmPurchaseDialogFragment.getType());
                        requests.add(r);
                        Bundle bundle = new Bundle();
                        bundle.putString("request", "1");
                        updateCoinNumber();
                        showFragmentWithBundle(R.id.main_activity_view, homeFragment, bundle);
                    } else {
                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (requestCode == 421 && resultCode == -1)  {  // Instagram feed returned
            String mediaId = data.getStringExtra("mediaId");
            String instagramId = data.getStringExtra("instagramId");
            int goal = data.getIntExtra("goal", -1);
            int cost = data.getIntExtra("cost", 0);
            String type = data.getStringExtra("type");
            JSONObject requestParams = new JSONObject();
            try {
                requestParams.put("socialContractId", getSocialContractId());
                requestParams.put("instagramId", instagramId);
                requestParams.put("mediaId", mediaId);
                requestParams.put("goal", goal);
                requestParams.put("type", type);
                requestParams.put("cost", cost);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = ServerDelegate.SERVER_URL + "/addInstagramQueue";
            final Context mContext = this;
            ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
                @Override
                public void onResult(boolean success, JSONObject response) throws JSONException {
                    if (success) {
                        Toast.makeText(mContext, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                        Request r = new Request(confirmPurchaseDialogFragment.getQuantity(),
                                confirmPurchaseDialogFragment.getType());
                        requests.add(r);
                        Bundle bundle = new Bundle();
                        bundle.putString("request", "1");
                        updateCoinNumber();
                        showFragmentWithBundle(R.id.main_activity_view, homeFragment, bundle);
                    } else {
                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            if (currFragment instanceof InitialConnectAccountFragment)
                initialConnectAccountFragment.onActivityResult(requestCode, resultCode, data);
            else if (currFragment instanceof DiscoverFragment)
                discoverFragment.onActivityResult(requestCode, resultCode, data);
            else if (currFragment instanceof DiscoverSettingsFragment)
                discoverSettingsFragment.onActivityResult(requestCode, resultCode, data);
        }

        String url = ServerDelegate.SERVER_URL + "/getQueue";
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("socialContractId", getSocialContractId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerDelegate.postRequest(this, url, requestParams, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray twitter = response.getJSONArray("twitter");
                JSONArray instagram = response.getJSONArray("instagram");
            }
        });
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
        currFragment = fragment;
    }

    private void showFragmentWithBundle(int viewId, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        currFragment = fragment;
    }

    public void updateCoinNumber() {
        String url = ServerDelegate.SERVER_URL + "/getCoins";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", getSocialContractId());
        ServerDelegate.postRequest(this, url, params,
                new ServerDelegate.OnResultListener() {
                    @Override
                    public void onResult(boolean success, JSONObject response) throws JSONException {
                        if (success) {
                            numCoins = response.getInt("coins");
                            TextView coinTV = findViewById(R.id.num_coins_tv);
                            String newCoinNumStr = "" + numCoins;
                            coinTV.setText(newCoinNumStr);
                        }
                    }
                });
    }

    private void updateCoinNumber(final ServerDelegate.OnResultListener listener) {
        String url = ServerDelegate.SERVER_URL + "/getCoins";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", getSocialContractId());
        ServerDelegate.postRequest(this, url, params,
                new ServerDelegate.OnResultListener() {
                    @Override
                    public void onResult(boolean success, JSONObject response) throws JSONException {
                        numCoins = response.getInt("coins");
                        TextView coinTV = findViewById(R.id.num_coins_tv);
                        String newCoinNumStr = "" + numCoins;
                        coinTV.setText(newCoinNumStr);
                        listener.onResult(success, response);
                    }
                });
    }

    public int getNumCoins() {
        return numCoins;
    }

    /**
     * Used when a user changes their email so that change can be displayed
     * @param newEmail - email to change to
     */
    public void setEmail(String newEmail) {
        email = newEmail;
        // update email in nav bar
        Menu menu = mDrawerList.getMenu();
        menu.findItem(R.id.nav_email).setTitle(email);

        // pass create updated bundle for profile fragment with new email
        Bundle bundle = new Bundle();
        bundle.putString("email", newEmail);
        bundle.putString("userId", userId);
        profileFragment = ProfileFragment.newInstance(bundle);
    }

    @Override
    public String getSocialContractId() {
        return userId;
    }


}
