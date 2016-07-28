package com.ereinecke.eatsafe.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Utility.Callback;
import com.ereinecke.eatsafe.util.Constants;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(Constants.MESSAGE_KEY) != null) {
                long barcode = intent.getLongExtra(Constants.RESULT_KEY, -1L);

                if (barcode != -1L) {
                    // TODO: Change to Snackbar or fancy toast
                    Toast.makeText(MainActivity.this,
                    intent.getStringExtra(Constants.MESSAGE_KEY), Toast.LENGTH_LONG).show();
                    launchProductFragment(barcode);
                } else {
                    Log.d(LOG_TAG, "In MessageReceiver, no valid barcode received.");
                }
            }
        }
    }

    public void launchProductFragment(long barcode) {

        Log.d(LOG_TAG, "in launchProductFragment, barcode = " + barcode);

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

