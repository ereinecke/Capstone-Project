package com.ereinecke.eatsafe.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import net.steamcrafted.loadtoast.LoadToast;

/**
 * Utility functions for EatSafe
 */

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final int MAX_UPC_LENGTH = 13;
    public static final int MIN_UPC_LENGTH = 8;



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
    private boolean checkCameraHardware(Context context) {
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
     * Callback interface for list item selection.
     */
    public interface Callback {
        void onItemSelected(String barcode);
    }

    /*
     * Interface to access LoadToast running in MainActivity.
     */
    public interface GetLoadToast {
        LoadToast getLoadToast();
    }

    /*
     * Interface to access LoadToast running in MainActivity.
     */
    public interface SetLoadToast {
        void setLoadToast(LoadToast lt);
    }
}


