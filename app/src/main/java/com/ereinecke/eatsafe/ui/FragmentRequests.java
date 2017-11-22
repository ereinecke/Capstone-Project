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
    public static void requestProductFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_PRODUCT_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with upload fragment display request */
    public static void requestUploadFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_UPLOAD_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with resultsFragment display request */
    public static void requestResultsFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_RESULTS_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with fragment display request */
    public static void requestSplashFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_SPLASH_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }
}
