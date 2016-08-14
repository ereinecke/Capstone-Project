package com.ereinecke.eatsafe.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;


/**
 * Utility functions for EatSafe
 */

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final int MAX_UPC_LENGTH = 13;
    public static final int MIN_UPC_LENGTH = 8;


    /* If the flag Constants.TEST_ADS is set true, generates a test AdRequest for emulators and
     * specified devices, otherwise returns a live AdRequest.  The AdRequest is set to
     * "is_designed_for_families".
     */
    public static AdRequest getAdRequest() {

        AdRequest request;

        if (Constants.TEST_ADS) {
            /* Generates a test AdRequest */
            request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // all emulators
                    .addTestDevice("6115000884")                  // ereinecke Nexus 5
                    .addTestDevice("03aac1722518fe55")            // ereinecke Pixel C
                    .build();
        } else {
            Bundle extras = new Bundle();
            extras.putBoolean("is_designed_for_families", true);
            request = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        }

        return request;
    }

    /* UPC validation checks for 8 to 13 numeric digits 0-9.  Even though android:inputType =
     * "number" enforces numeric entry, that could include '-' and '.'.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean validateBarcode(String barcode) {

        if (barcode.length() > MAX_UPC_LENGTH || barcode.length() < MIN_UPC_LENGTH) {
            Log.d(LOG_TAG, "Bad barcode length: " + barcode.length());
            return false;
        } else if (barcode.contains(".")) {
            Log.d(LOG_TAG, "Barcode contains \'.\'");
            return false;
        }
        return true;
    }

    /** Check if this device has a camera */
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            return false;
        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard(Context context) {

            InputMethodManager inputMethodManager = (InputMethodManager)
                   context.getSystemService(Context.INPUT_METHOD_SERVICE);
            // inputMethodManager.hideSoftInputFromWindow(getActivity()
            //         .getCurrentFocus().getWindowToken(), 0);
     }

    /*
     * Callback interface for product list item selection.
     */
    public interface Callback {
        void onItemSelected(String barcode);
    }


}


