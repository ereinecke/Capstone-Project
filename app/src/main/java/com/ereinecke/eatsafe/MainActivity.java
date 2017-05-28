package com.ereinecke.eatsafe;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.commonsware.cwac.provider.StreamProvider;
import com.ereinecke.eatsafe.services.OpenFoodService;
import com.ereinecke.eatsafe.ui.ProductFragment;
import com.ereinecke.eatsafe.ui.SearchFragment;
import com.ereinecke.eatsafe.ui.SplashFragment;
import com.ereinecke.eatsafe.ui.TabPagerFragment;
import com.ereinecke.eatsafe.ui.UploadDialog;
import com.ereinecke.eatsafe.ui.UploadFragment;
import com.ereinecke.eatsafe.ui.UploadFragment.PhotoRequest;
import com.ereinecke.eatsafe.util.App;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility.Callback;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements Callback, PhotoRequest,
        UploadDialog.NoticeDialogListener {
    // DeleteDialog.NoticeDialogListenter, {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean isTablet = false;
    private long barcode = Constants.BARCODE_NONE; // most recent scanned or entered barcode
    private int currentFragment = 0;
    private String photoReceived;
    private View rootView;
    private TabPagerFragment tabPagerFragment;
    private BroadcastReceiver messageReceiver;
    private final IntentFilter messageFilter = new IntentFilter(Constants.MESSAGE_EVENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        boolean scanner = false;

        setContentView(R.layout.activity_main);
        rootView = findViewById(android.R.id.content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));

        // See if activity was started by widget
        Intent intent = getIntent();  // may not be needed here
        if (intent != null) {
            Log.d(LOG_TAG, "in onCreate(), intent: " + intent.toString());
            String message = intent.getStringExtra(Constants.MESSAGE_KEY);
            if (message != null && message.equals(Constants.ACTION_SCAN_BARCODE)) {
                scanner = true;
            }
        }

        // messageReceiver catches barcode from service or fragment display requests.
        messageReceiver = new MessageReceiver();

        isTablet = (findViewById(R.id.dual_pane) != null);

        if (findViewById(R.id.tab_container) != null) {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.CURRENT_FRAGMENT, currentFragment);
            tabPagerFragment = new TabPagerFragment();
            tabPagerFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tab_container, new TabPagerFragment()).commit();

            // Handle right-hand pane on dual-pane layouts
            if (isTablet && (savedInstanceState == null)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.right_pane_container, new SplashFragment())
                        .commit();
            }

            if (scanner) {
                launchScannerIntent();
            }
        }
    }

    @Override
    public void onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
        super.onResume();
    }

    @Override
    public void onDestroy() {

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    // TODO: Need to make back arrow disappear when TabPagerFragment is showing.
    //    @Override
    //    public void onStart() {
    //
    //        super.onStart();
    //    }

    /* Not sure this is needed */
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        Log.d(LOG_TAG, "BackStackEntryCount: "+ count);

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(LOG_TAG, "upload dialog positive click relayed to MainActivity");
        launchUploadFragment();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(LOG_TAG, "upload dialog negative click relayed to MainActivity");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(Constants.CURRENT_PHOTO, photoReceived);
        savedInstanceState.putInt(Constants.CURRENT_FRAGMENT, currentFragment);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getString(Constants.CURRENT_PHOTO);
        savedInstanceState.getIntegerArrayList(Constants.CURRENT_FRAGMENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // respond to the action bar's Up/Home button

            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate();
                return true;

            case R.id.action_scan:
                launchScannerIntent();
                return true;

            //noinspection SimplifiableIfStatement
            case R.id.action_login:
                Snackbar.make(rootView, getString(R.string.no_login_yet), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return true;

            case R.id.action_sensitivities:
                Snackbar.make(rootView, getString(R.string.no_sensitivities_yet), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(String barcode) {

        try {
            long productId = Long.parseLong(barcode);
            launchProductFragment(productId);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Selected item contains unparseable barcode: " + barcode);
            Log.e(LOG_TAG, e.toString());
        }
    }
    
    /* MessageReceiver is listening for any of the following:
     *   - an intent from OpenFoodService, containing a product barcode and a result string.
     *   - intents from various fragments, requesting that other fragments be displayed.
     */

    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "in onReceive(), intent: " + intent.getAction());

            // Assume any other message key will be the barcode
            // Barcode returned from OpenFoodService
            if (intent.getAction().equals(Constants.MESSAGE_EVENT)) {

                String messageKey = intent.getStringExtra(Constants.MESSAGE_KEY);
                Log.d(LOG_TAG, "messageKey: " + messageKey);

                switch (messageKey) {

                    case Constants.BARCODE_KEY:
                        barcode = intent.getLongExtra(Constants.RESULT_KEY, Constants.BARCODE_NOT_FOUND);

                        Log.d(LOG_TAG, "MessageReceiver result: " + messageKey);
                        Snackbar.make(rootView, messageKey, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                        if (barcode == Constants.BARCODE_NOT_FOUND) {
                            launchSplashFragment();
                            UploadDialog uploadDialog = new UploadDialog();
                            uploadDialog.show(getSupportFragmentManager(), getString(R.string.upload));

                        } else {
                            launchProductFragment(barcode);
                        }
                        break;
                    case Constants.ACTION_BACK_PRESS:
                        onBackPressed();
                        break;

                    case Constants.ACTION_UPLOAD_FRAGMENT:
                        launchUploadFragment();
                        break;

                    case Constants.ACTION_RESULTS_FRAGMENT:
                        launchResultsFragment();
                        break;

                    case Constants.ACTION_PRODUCT_FRAGMENT:
                        launchProductFragment(barcode);
                        break;

                    case Constants.ACTION_SEARCH_FRAGMENT:
                        // launchSearchFragment();
                        break;

                    case Constants.ACTION_SPLASH_FRAGMENT:
                        launchSplashFragment();
                        break;
                }
            }
        }
    }

    /* Receives intent results from activities such as Camera, Gallery or Scanner  */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (intent == null) {
            Log.d(LOG_TAG, "in onActivityResult, null intent received");
            Log.d(LOG_TAG, "   requestCode: " + requestCode);
            Log.d(LOG_TAG, "   resultCode: " + resultCode);
            return;
        }
        Log.d(LOG_TAG, "requestCode: " + requestCode + "; resultCode: " + resultCode +
                "; intent: " + intent.toString());

        switch (requestCode) {

            case IntentIntegrator.REQUEST_CODE: {
            /* Catching result from barcode scan */
                if (resultCode == RESULT_OK) {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                    Log.d(LOG_TAG, "IntentResult: " + result.toString());

                    if (result != null) {
                        String barcode = result.getContents();
                        if (result.getContents() == null) {
                            Snackbar.make(rootView, getString(R.string.result_failed), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {
                            Log.d(LOG_TAG, "Scan result: " + result.toString());
                            // Have a (potentially) valid barcode, update text view and fetch product info
                            SearchFragment.handleScanResult(barcode);
                            Intent productIntent = new Intent(this, OpenFoodService.class);
                            productIntent.putExtra(Constants.BARCODE_KEY, barcode);
                            productIntent.setAction(Constants.ACTION_FETCH_PRODUCT);
                            startService(productIntent);
                        }
                    }
                } else {
                    Log.d(LOG_TAG, "Error scanning barcode: " + resultCode);
                }
                break;
            }

            case Constants.CAMERA_IMAGE_REQUEST: {
            /* Catching result from camera */
                if (resultCode == RESULT_OK) {
                    scanMedia(photoReceived);
                    UploadFragment.updateImage(photoReceived);
                } else { // capture image request came back with error
                    Log.d(LOG_TAG, "Error taking photo: " + resultCode);
                }
                break;
            }

            case Constants.GALLERY_IMAGE_REQUEST: {
            /* Catching result from gallery */

                InputStream imageStream;
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = intent.getData();
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        Log.d(LOG_TAG, "Gallery image request returned Uri: " + imageUri);
                        UploadFragment.updateImageFromGallery(imageUri);
                    } catch (FileNotFoundException e) {
                        Log.d(LOG_TAG, e.getMessage());
                    }

                } else {
                    Log.d(LOG_TAG, "Error picking photo from gallery: " + resultCode);
                }
                break;
            }

            default: {
                Log.d(LOG_TAG, "Unexpected requestCode received: " + requestCode);
                // super.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

    public long getBarcode() {
        return barcode;
    }

    @Override
    public String PhotoRequest(int source, int photo) {

        // TODO: check to make sure we have camera permissions here.
        Log.d(LOG_TAG, "PhotoRequest(" + photo + ") received.");
        photoReceived = "";
        if (source == Constants.CAMERA_IMAGE_REQUEST) {
            launchPhotoIntent(photo);
        } else if (source == Constants.GALLERY_IMAGE_REQUEST) {
            launchGalleryIntent(photo);
        }
        return photoReceived;
    }

    /* Moves upload fragment to front   */
    private void launchUploadFragment() {
        Log.d(LOG_TAG, "in launchUploadFragment()");
        if (findViewById(R.id.tab_container) != null) {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.CURRENT_FRAGMENT, Constants.FRAG_UPLOAD);
            TabPagerFragment tabPagerFragment = new TabPagerFragment();
            tabPagerFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tab_container, tabPagerFragment).commit();
        }

    }

    /* Moves results fragment to front   */
    private void launchResultsFragment() {
        Log.d(LOG_TAG, "in launchResultsFragment()");
        if (findViewById(R.id.tab_container) != null) {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.CURRENT_FRAGMENT, Constants.FRAG_RESULTS);
            TabPagerFragment tabPagerFragment = new TabPagerFragment();
            tabPagerFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tab_container, tabPagerFragment).commit();
        }

    }

    private void launchProductFragment(long barcode) {

        // Pass barcode to ProductFragment
        if (barcode != Constants.BARCODE_NOT_FOUND) {
            Bundle args = new Bundle();
            args.putLong(Constants.BARCODE_KEY, barcode);

            ProductFragment ProductFragment = new ProductFragment();
            ProductFragment.setArguments(args);

            // TODO: Figure out intermittent crash at this call
            // IllegalStateException: Can not perform this action after onSaveInstanceState
            if (!isTablet) {  // productFragment replaces TabPagerFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tab_container, ProductFragment)
                        .addToBackStack(null)
                        .commit();
            } else {  // productFragment replaces splashFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.right_pane_container, ProductFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            Log.d(LOG_TAG, "Barcode not found, not launching product fragment.");
        }
    }

    /* Puts up launchFragment.  Should only do in dual-pane mode, otherwise only menu items are
     * available.
     */
    private void launchSplashFragment() {
        // Handle right-hand pane on dual-pane layouts
        if (isTablet) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_pane_container, new SplashFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tab_container, new SplashFragment())
                    .commit();
        }
    }



    /* updates history list and brings ResultsFragment tab to the front in dual-pane mode */
    public void updateResultsFragment() {

    }

    public void launchPhotoIntent(int whichPhoto) {

        Log.d(LOG_TAG, "Launching intent for photo #" + whichPhoto);
        // create Intent to take a picture and return control to the calling application
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = openOutputMediaFile();
            Log.d(LOG_TAG, "photoReceived: " + photoReceived);

            if (photoFile != null) {

                Uri photoUri = StreamProvider
                        .getUriForFile("com.ereinecke.eatsafe.fileprovider", photoFile);
                Log.d(LOG_TAG, "photoUri: " + photoUri.toString());
                // set the image file name
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureIntent.putExtra(Constants.WHICH_PHOTO, whichPhoto);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // start the image capture Intent
                startActivityForResult(takePictureIntent,
                        Constants.CAMERA_IMAGE_REQUEST);
            }
        }
    }

    public void launchGalleryIntent(int whichPhoto) {

        Log.d(LOG_TAG, "Launching intent for photo #" + whichPhoto);
        // create Intent to take a picture and return control to the calling application
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK);
        pickPictureIntent.setType("image/*");

        startActivityForResult(pickPictureIntent, Constants.GALLERY_IMAGE_REQUEST);
    }


    /* Launches the barcode scanner by replacing the SearchFragment with a new SearchFragment
     * with an action as an extra.
     */
    private void launchScannerIntent() {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MESSAGE_KEY, Constants.ACTION_SCAN_BARCODE);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.search_fragment, searchFragment).commit();

    }


    /** Returns a unique, opened file for image; sets photoReceived with filespec */

    public File openOutputMediaFile(){

        String appName = App.getContext().getString(R.string.app_name);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), appName);
        Log.d(LOG_TAG, "mediaStorageDir: " + mediaStorageDir.toString());

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "failed to create directory " + mediaStorageDir);
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = Constants.IMG_PREFIX + timeStamp;
        File imageFile = null;

        // Open a temp file to pass to Camera
        try {
            imageFile = File.createTempFile(fileName, ".jpg", mediaStorageDir);
            Log.d(LOG_TAG, "imageFile: " + imageFile);
        } catch(IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }

        // Generate a file: path for use with intent
        if (imageFile != null) {
            photoReceived = imageFile.getAbsolutePath();
        }
        return imageFile;
    }

    /**
     * Sends a broadcast to have the media scanner scan a file
     *
     * @param path
     *            the file to scan
     */
    private void scanMedia(String path) {

        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }
}

