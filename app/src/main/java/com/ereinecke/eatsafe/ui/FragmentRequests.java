package com.ereinecke.eatsafe.ui;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ereinecke.eatsafe.util.App;
import com.ereinecke.eatsafe.util.Constants;

/**
 * Utility functions to bring specific fragments to the fore.
 */

public class FragmentRequests {

    /* Broadcast Intent to MainActivity.MessageReceiver with product display request */
    public void requestProductFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_PRODUCT_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with resultsFragment display request */
    public void requestResultsFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_RESULTS_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with fragment display request */
    public void requestSplashFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_SPLASH_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }
}
