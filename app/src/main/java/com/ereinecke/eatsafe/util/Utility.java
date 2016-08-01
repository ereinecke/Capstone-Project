package com.ereinecke.eatsafe.util;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ereinecke.eatsafe.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Utility functions for EatSafe
 */

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final int MAX_UPC_LENGTH = 13;
    public static final int MIN_UPC_LENGTH =  8;

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri() {
        Uri fileUri = null;

        try {
            fileUri = Uri.fromFile(getOutputMediaFile());
        } catch(NullPointerException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return fileUri ;
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(){

        String appName = App.getContext().getString(R.string.app_name);
        Log.d(LOG_TAG, "App name: " + appName);
        if (!Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            return null;
        }

        // Photos will be stored in a subdirectory (app_name) under the photos directory
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appName);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
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


    /*
     * Callback interface for list item selection.
     */
    public interface Callback {
        void onItemSelected(String barcode);
    }
}
