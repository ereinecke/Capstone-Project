package com.ereinecke.eatsafe.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ereinecke.eatsafe.R;

/**
 * ResultsFragment shows all products stored in ContentReceiver.  In future, there will be a
 * search function to return specific items.
 *
 * // TODO: Add search (product_name || brand)
 */
public class ResultsFragment extends Fragment  {
    private static final String LOG_TAG = ResultsFragment.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private EditText barcode;
    private static View rootView;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }


}
