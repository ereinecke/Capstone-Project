package com.ereinecke.eatsafe.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Utility functions for EatSafe
 */

public class Utility {
    public static final int UPC_LENGTH = 12;

    /* Launch barcode scanner
    */

    public static void launchScanner(View view) {

        // TODO: Remove after debugging
        Snackbar.make(view, "Scanner should launch", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /* UPC validation checks for 12 numeric digits 0-9.  Even though android:inputType = "number"
     * enforces numeric entry, that could include '-' and '.'.
     */
    public static boolean validateUPC(String barcode) {
        int numericCode;

        if (barcode.length() != UPC_LENGTH) return false;

        /* positive integers only allowed */
        try {
            numericCode = Integer.parseInt(barcode);
        }
        catch (NumberFormatException nfe) {
            return false;
        }

        if (numericCode > -1) return true;
        else return false;
    }
}
