package com.ereinecke.eatsafe.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.ereinecke.eatsafe.util.Utility.Logd;

/**
 * TabPagerFragment holds ViewPager with tabbed views for search, upload and results
 * The desired tab is passed as an int in an argument, and we don't need to maintain the
 * tab selection in this method.
 *
 */
public class TabPagerFragment extends Fragment {

    private static final String LOG_TAG = TabPagerFragment.class.getSimpleName();


    public TabPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int whichFragment;

        try {
            whichFragment = getArguments().getInt(Constants.CURRENT_FRAGMENT);
        } catch(Exception e) {
            Logd(LOG_TAG, "Current fragment parameter not found in onCreateView().");
            whichFragment = 0;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_pager, container, false);

        ViewPager viewPager = view.findViewById(R.id.fragment_tab_pager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(whichFragment);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.search));
        adapter.addFragment(new UploadFragment(), getResources().getString(R.string.upload));
        adapter.addFragment(new ResultsFragment(), getResources().getString(R.string.results));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
