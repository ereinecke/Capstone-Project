package com.ereinecke.eatsafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ereinecke.eatsafe.services.OpenFoodService;
import com.ereinecke.eatsafe.ui.ProductFragment;
import com.ereinecke.eatsafe.ui.TabPagerFragment;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility.Callback;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity implements Callback {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean isTablet = false;
    private BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(Constants.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            TabPagerFragment tabPagerFragment = new TabPagerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, tabPagerFragment).commit();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            return true;
        }

        return id == R.id.action_sensitivities || super.onOptionsItemSelected(item);
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
                View rootView = findViewById(android.R.id.content);

                if (barcode != -1L) {
                   Snackbar.make(rootView, getString(R.string.barcode_found, barcode),
                            Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                    launchProductFragment(barcode);
                } else {
                    // TODO: Give Snackbar an action to launch UploadFragment or not
                    Log.d(LOG_TAG, "In MessageReceiver, no valid barcode received.");
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.barcode_not_found),
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        }
    }

    /* Receives intents.  If FETCH_PRODUCT, it's the result of a product search */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Log.d(LOG_TAG, "Scan result: " + result.toString());
                // Have a (potentially) valid barcode, fetch product info
                Intent productIntent = new Intent(this, OpenFoodService.class);
                productIntent.putExtra(Constants.BARCODE_KEY, result.getContents().toString());
                productIntent.setAction(Constants.FETCH_PRODUCT);
                startService(productIntent);

            } else {
                Log.d(LOG_TAG, "Scan failed");
            }
        }
    }

    public void launchProductFragment(long barcode) {

        // Turn on back button in ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Pass barcode to ProductFragment
        if (barcode != -1L) {
            Bundle args = new Bundle();
            args.putLong(Constants.BARCODE_KEY, barcode);

            ProductFragment ProductFragment = new ProductFragment();
            ProductFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProductFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

