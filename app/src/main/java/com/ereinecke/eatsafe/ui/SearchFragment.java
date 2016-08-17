package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.services.OpenFoodService;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * SearchFragment allows the user to search the database by UPC code.  The code can be scanned
 * or entered manually.
 */

public class SearchFragment extends Fragment {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private static final String BARCODE_CONTENT = "barcodeContent";
    private EditText barcodeView;
    private boolean startScan = false;
    private static View rootView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null &&
                getArguments().getString(Constants.MESSAGE_KEY).equals(Constants.ACTION_SCAN_BARCODE))
            startScan = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        barcodeView = (EditText) rootView.findViewById(R.id.barcode);

        barcodeView.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            runSearch();
                            return true; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });

        /* Search button */
        ImageButton searchButton = (ImageButton) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runSearch();
            }
        });

        /* Scan barcode button  */
        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               runScan();
            }
        });

        /* Ad displayed in bottom toolbar, respecting Constants.TEST_ADS flag  */
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = Utility.getAdRequest();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        } else {
            Log.d(LOG_TAG, "adView not found");
        }

        if (savedInstanceState != null) {
            barcodeView.setText(savedInstanceState.getString(BARCODE_CONTENT));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (barcodeView != null) {
            outState.putString(BARCODE_CONTENT, barcodeView.toString());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (startScan) runScan();
    }

    private void runScan() {

        // ZXing is called here
        if (checkConnectivity()) {
            try {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
            } catch (Exception e) {
                Snackbar.make(rootView, getActivity().getString(R.string.result_failed),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private void runSearch() {

        String barcodeStr = barcodeView.getText().toString();

        if (!Utility.validateBarcode(barcodeStr)) {
            Snackbar.make(rootView, getString(R.string.barcode_validation_failed, barcodeStr),
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (checkConnectivity()) {
            // Have a (potentially) valid barcode, fetch product info
            callFetchProduct(barcodeStr);
        }
    }

    public static void handleScanResult(String result) {

        ((TextView) rootView.findViewById(R.id.barcode)).setText(result);
    }

    /* Sends an intent to OpenFoodService to fetch product info */
    public void callFetchProduct(String barcodeStr) {
        Log.d(LOG_TAG, "in callFetchProduct: " + barcodeStr);

        // Have a (potentially) valid barcode, fetch product info
        Intent productIntent = new Intent(getActivity(), OpenFoodService.class);
        productIntent.putExtra(Constants.BARCODE_KEY, barcodeStr);
        productIntent.setAction(Constants.ACTION_FETCH_PRODUCT);
        getActivity().startService(productIntent);
     }

    /* Returns a boolean representing internet connectivity */
    private boolean checkConnectivity() {
        // Check to see if internet connection available
        Context context = getActivity();
        CharSequence text;
        boolean isConnected;

        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        text = getString(R.string.no_internet);
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;
        } else {
            isConnected = false;
            Snackbar.make(rootView, text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        return isConnected;
    }
}
