package com.ereinecke.eatsafe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ereinecke.eatsafe.MainActivity;
import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

/**
 * Widget Provider for EatSafe.  When clicking on the widget, EatSafe is launched and a barcode
 * scan is initiated.
 */
public class EatSafeWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = EatSafeWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        // The following assumes a single widget for EatSafe; otherwise, loop through appWidgetIds
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[0];

            Log.d(LOG_TAG, "in onUpdate()");

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_SCAN_BARCODE);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}