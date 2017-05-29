package com.ereinecke.eatsafe.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Utility functions for EatSafe
 */

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static final int MAX_UPC_LENGTH = 13;
    private static final int MIN_UPC_LENGTH = 8;


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
                    .addTestDevice("6115000884")                  // ereinecke Nexus 5 (API 23)
                    .addTestDevice("03aac1722518fe55")            // ereinecke Pixel C (API N)
                    .addTestDevice("c08083719a0fa21")             // ereinecke Galaxy Tab 2 (API 17)
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
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
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

    public static String compressImage(String url) {
        File fileFront = new File(url);
        Bitmap bt = decodeFile(fileFront);
        if (bt == null) {
            Log.e("COMPRESS_IMAGE", url + " not found");
            return null;
        }

        File smallFileFront = new File(url.replace(".png", "_small.png"));
        OutputStream fOutFront = null;
        try {
            fOutFront = new FileOutputStream(smallFileFront);
            bt.compress(Bitmap.CompressFormat.PNG, 100, fOutFront);
        } catch (IOException e) {
            Log.e("COMPRESS_IMAGE", e.getMessage(), e);
        } finally {
            if (fOutFront != null) {
                try {
                    fOutFront.flush();
                    fOutFront.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }
        return smallFileFront.toString();
    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 300;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /*
     * Callback interface for product list item selection.
     */
    public interface Callback {
        void onItemSelected(String barcode);
    }


}


