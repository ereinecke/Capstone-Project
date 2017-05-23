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

import java.util.ArrayList;
import java.util.List;

/**
 * TabPagerFragment holds ViewPager with tabbed views for search, upload and results
 *
 */
public class TabPagerFragment extends Fragment {

    private ViewGroup container;
    private ViewPager viewPager;


    public TabPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_pager, container, false);

        this.container = container;

        viewPager = (ViewPager) view.findViewById(R.id.tab_pager_fragment);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
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

    public void setUploadFragment() {
        // TODO: need to setCurrentItem on the viewPager but need to initialize outside of onCreateView()
        // ?? Should i be doing this in onViewCreated()??
        /*
        if (viewPager == null) {
            viewPager = (ViewPager) rootView.findViewById(R.id.tab_pager_fragment);
            setupViewPager(viewPager);
        }
        viewPager.setCurrentItem(1);
        */
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
