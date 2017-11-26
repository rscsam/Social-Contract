/**
 * Certain portions of this activity were inspired by or derivative of
 * Suleiman19/Android-Material-Design-for-pre-Lollipop
 *
 * Copyright notice below
 *
 * Copyright 2017 Suleiman Ali Shakir

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License.
 */



package jd7337.socialcontract.controller.activity;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.fragment.EditInterestProfileFragment;
import jd7337.socialcontract.controller.fragment.EditInterestProfilePromptFragment;
import jd7337.socialcontract.controller.fragment.InitialConnectAccountFragment;

public class Tutorial2Activity extends AppCompatActivity implements
        InitialConnectAccountFragment.InitialConnectAccountFListener,
        EditInterestProfilePromptFragment.EditInterestProfilePromptFListener,
        EditInterestProfileFragment.EditInterestProfileFListener {

    private InitialConnectAccountFragment icaFragment;
    private EditInterestProfilePromptFragment eippFragment;
    private EditInterestProfileFragment eipFragment;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    ImageButton mNextButton;
    Button mSkipButton, mFinishButton;
    ImageView zero, one, two, three, four, five;
    ImageView[] indicators;
    int page = 0;

    CoordinatorLayout mCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial2);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        icaFragment = new InitialConnectAccountFragment();
        eippFragment = new EditInterestProfilePromptFragment();
        eipFragment = new EditInterestProfileFragment();

        mNextButton = (ImageButton) findViewById(R.id.intro_next_button);
        mSkipButton = (Button) findViewById(R.id.intro_skip_button);
        mFinishButton = (Button) findViewById(R.id.intro_finish_button);

        zero = (ImageView) findViewById(R.id.intro_indicator_0);
        one = (ImageView) findViewById(R.id.intro_indicator_1);
        two = (ImageView) findViewById(R.id.intro_indicator_2);
        three = (ImageView) findViewById(R.id.intro_indicator_3);
        four = (ImageView) findViewById(R.id.intro_indicator_4);
        five = (ImageView) findViewById(R.id.intro_indicator_5);
        indicators = new ImageView[]{zero, one, two, three, four, five};

        mCoordinator = (CoordinatorLayout) findViewById(R.id.tutorial2_activity);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.intro_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = getResources().getColor(R.color.colorAltDark);
        final int color2 = getResources().getColor(R.color.colorPrimary);
        final int color3 = getResources().getColor(R.color.colorAltLight);
        final int[] colorList = new int[]{color1, color2, color3};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset,
                        colorList[position % 3], colorList[(position + 1) % 3]);
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);

                if (position % 3 == 0) {
                    mViewPager.setBackgroundColor(color1);
                } else if (position % 3 == 1) {
                    mViewPager.setBackgroundColor(color2);
                } else {
                    mViewPager.setBackgroundColor(color3);
                }

                mNextButton.setVisibility(position == 5 ? View.GONE : View.VISIBLE);
                mFinishButton.setVisibility(position == 5 ? View.VISIBLE : View.GONE);
                mSkipButton.setVisibility(position == 5 ? View.GONE : View.VISIBLE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startMainActivity();
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startMainActivity();
            }
        });

    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView image;
        TextView title;
        TextView text;

        int[] images = new int[]{R.drawable.ic_public_white_48dp, R.drawable.ic_search_white_48dp, R.drawable.ic_trending_up_white_48dp,
                R.drawable.coin_icon};
        int[] titles = new int[] {R.string.intro_welcome, R.string.intro_discover, R.string.intro_grow, R.string.intro_coins};
        int[] texts = new int[] {R.string.intro_welcome_text, R.string.intro_discover_text, R.string.intro_grow_text, R.string.intro_coins_text};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial2, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.intro_image);
            imageView.setImageResource(images[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            TextView labelText = (TextView) rootView.findViewById(R.id.intro_label);
            labelText.setText(titles[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            TextView bodyText = (TextView) rootView.findViewById(R.id.intro_text);
            bodyText.setText(texts[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class above).
            if (position < 4) {
                return PlaceholderFragment.newInstance(position + 1);
            } else if (position == 4){
                return new EditInterestProfilePromptFragment();
            } else {
                return new InitialConnectAccountFragment();
            }

        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }
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

    @Override
    public void onClickICAFConnectAccount() {
        showFragment(R.id.tutorial2_activity, eippFragment);
    }

    @Override
    public void onClickEIPPEditProfile() {
        showFragment(R.id.tutorial2_activity, eipFragment);
    }

    @Override
    public void onClickEIPPSkip() {
        startMainActivity();
    }

    @Override
    public void onClickEIPSubmit() {
        startMainActivity();
    }

}
