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
import com.ereinecke.eatsafe.ui.UploadFragment;
import com.ereinecke.eatsafe.ui.UploadFragment.PhotoRequest;
import com.ereinecke.eatsafe.util.App;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility.Callback;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/* TODO: Need to tell users to provide Camera and Storage permissions on API > 23  */

public class MainActivity extends AppCompatActivity implements Callback, PhotoRequest {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean isTablet = false;
    private String photoReceived;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(android.R.id.content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BroadcastReceiver messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(Constants.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        isTablet = (findViewById(R.id.dual_pane) != null);

        if (findViewById(R.id.tab_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            TabPagerFragment tabPagerFragment = new TabPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tab_container, tabPagerFragment).commit();

            if (isTablet) {
                SplashFragment splashFragment = new SplashFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.right_pane_container, splashFragment).commit();
            }
        }
    }

    // TODO: Need to make back arrow disappear when TabPagerFragment is showing.  This hides only
    // after a rotation.
    @Override
    public void onStart() {
        super.onStart();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(Constants.CURRENT_PHOTO, photoReceived);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getString(Constants.CURRENT_PHOTO);
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

            //noinspection SimplifiableIfStatement
            case R.id.action_login:
                return true;

            case R.id.action_sensitivities:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(String barcode) {
        try {
            long productId = Long.parseLong(barcode);
            launchProductFragment(productId);
        } catch(NumberFormatException e) {
            Log.e(LOG_TAG, "Selected item contains unparseable barcode: " + barcode);
            Log.e(LOG_TAG, e.toString());
        }
    }
    
    /* MessageReceiver is listening for an intent from OpenFoodService, containing a product
     *   barcode
     */
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(Constants.MESSAGE_KEY) != null) {
                long barcode = intent.getLongExtra(Constants.RESULT_KEY, -1L);
                if (barcode != -1L) {
                   Snackbar.make(rootView, getString(R.string.barcode_found, barcode),
                            Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                    launchProductFragment(barcode);
                } else {
                    // TODO: Give Snackbar an action to launch UploadFragment or not
                    Log.d(LOG_TAG, "In MessageReceiver, no valid barcode received.");
                    Snackbar.make(rootView,
                            getString(R.string.barcode_not_found),
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        }
    }

    /* Receives intent results.  */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
                            Snackbar.make(rootView, getString(R.string.result_failed),
                                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

                final InputStream imageStream;
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
                super.onActivityResult(requestCode, resultCode, intent);
            }
        }
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

    public void launchProductFragment(long barcode) {

        if (!isTablet) {
            // Turn on back button in ActionBar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Pass barcode to ProductFragment
        if (barcode != -1L) {
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
                        .commit();
            }
        }
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


    /** Returns a unique, opened file for image; sets photoReceived with filespec */
    public  File openOutputMediaFile(){

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

