package com.ereinecke.eatsafe.util;

import android.util.Log;

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

    /*
     * Callback interface for list item selection.
     */
    public interface Callback {
        public void onItemSelected(String barcode);
    }
}
