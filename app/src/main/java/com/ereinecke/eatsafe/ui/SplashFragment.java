package com.ereinecke.eatsafe.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Utility;

/**
 * This fragment displays the OpenFoodFacts logo, as a splash screen of sorts.  This only displays
 * on tablets in the right-hand pane until a ProductFragment displays there.
 */
public class SplashFragment extends Fragment {

    View rootView;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_splash, container, false);

        // Button listener - launch OpenFoodFacts.org in a WebView
        rootView.findViewById(R.id.offLogoView).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                Utility.requestWebView(getString(R.string.about_off), getString(R.string.off_domain));
            }
        });

        return rootView;
    }


}
