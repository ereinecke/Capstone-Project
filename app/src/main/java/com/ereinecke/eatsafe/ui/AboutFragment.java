package com.ereinecke.eatsafe.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ereinecke.eatsafe.R;


/**
 * This fragment displays the an About fragment, with some About Eatsafe text and an AboutLibsFragment
 * that shows what libraries are used.
 */

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = getContext();
        String version;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        // Get version number
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            version = "";
        }

        TextView aboutText = rootView.findViewById(R.id.about_text);
        aboutText.setText(getString(R.string.about_text, version));

        return rootView;
    }


}