package com.ereinecke.eatsafe.util;

import android.app.Application;
import android.content.Context;

/**
 * App is used to retrieve a context for classes not part of an Activity or Fragment.
 * This is necessary to pull resources from res files.
 * http://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
