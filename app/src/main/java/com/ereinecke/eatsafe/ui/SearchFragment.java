package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.services.OpenFoodService;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;

/**
 * SearchFragment allows the user to search the database by UPC code.  The code can be scanned
 * or entered manually.
 */

public class SearchFragment extends Fragment {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private static final String BARCODE_CONTENT = "barcodeContent";
    private EditText barcode;
    private static View rootView;

    public SearchFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        barcode = (EditText) rootView.findViewById(R.id.barcode);

        barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String barcodeStr = s.toString();

                if (!Utility.validateBarcode(barcodeStr)) {
                    // TODO: This Snackbar should probably be LENGTH_INDEFINITE
                    Snackbar.make(rootView, getString(R.string.barcode_validation_failed, barcodeStr),
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setDuration(5000).show();
                  } else if (checkConnectivity()) {
                        // Have a (potentially) valid barcode, fetch product info
                        Intent bookIntent = new Intent(getActivity(), OpenFoodService.class);
                        bookIntent.putExtra(Constants.BARCODE, barcodeStr);
                        bookIntent.setAction(Constants.FETCH_PRODUCT);
                        getActivity().startService(bookIntent);
                }
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ZXing is called here
                Context context = getActivity();

                // TODO: IntentIntegrator part of ZXing, currently not being recognized.
                /*
                if (checkConnectivity()) {
                    try {
                        IntentIntegrator integrator = new IntentIntegrator(getActivity());
                        integrator.initiateScan();
                    } catch (Exception e) {
                        Snackbar.make(view, getActivity().getString(R.string.result_failed),
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } */
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.launchScanner(view);
            }
        });

        if (savedInstanceState != null) {
            barcode.setText(savedInstanceState.getString(BARCODE_CONTENT));
        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (barcode != null) {
            outState.putString(BARCODE_CONTENT, barcode.getText().toString());
        }
    }

    /* Returns a boolean representing internet connectivity */
    private boolean checkConnectivity() {
        // Check to see if internet connection available
        Context context = getActivity();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        boolean isConnected;

        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        text = getString(R.string.no_internet);
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;
        } else {
            isConnected = false;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        return isConnected;
    }
}
