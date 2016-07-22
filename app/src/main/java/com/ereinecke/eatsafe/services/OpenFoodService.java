package com.ereinecke.eatsafe.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class OpenFoodService extends IntentService {

    private final String LOG_TAG = OpenFoodService.class.getSimpleName();


    public OpenFoodService() {
        super("EatSafe");   // TODO: ???
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.FETCH_PRODUCT.equals(action)) {
                final String barcode = intent.getStringExtra(Constants.BARCODE);
                fetchProduct(barcode);
            }
        }
    }


    /**
     * Handle action fetchProduct in the provided background thread with the provided
     * parameters.
     */
    private void fetchProduct(String barcode) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String productJsonString = null;

        if (!Utility.validateBarcode(barcode)) {
            return;
        }

        /* Get from local database if present
         * TODO: Uncomment once OpenFoodProvider is done

        Cursor productEntry = getContentResolver().query(
                OpenFoodContract.ProductEntry.buildProjectUri(Long.parseLong(barcode)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if(productEntry.getCount() > 0){
            productEntry.close();
            return;
        }

        productEntry.close();
        */

        /* pull down JSON */

        /*  The below is a framework from Alexandria as a starting point */

        try {
            Uri builtUri = Uri.parse(Constants.PRODUCTS_BASE_URL).buildUpon()
                    .appendPath(barcode + ".json")
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL requested: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            Log.d(LOG_TAG, "URLConnection: " + urlConnection.toString());

            try {
                urlConnection.connect();
            } catch (Exception e) {
                Log.d(LOG_TAG, "Network connection error");
                return;
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.d(LOG_TAG, "Input buffer null");
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            productJsonString = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        Log.d(LOG_TAG, productJsonString);

        /*  TODO: JSON parsing happens here
          try {
            JSONObject productJson = new JSONObject(productJsonString);
            JSONArray productArray;
            if(productJson.has(ITEMS)){
                productArray = productJson.getJSONArray(ITEMS);
            } else {
                Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
                messageIntent.putExtra(Constants.MESSAGE_KEY,getResources().getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            String title = productInfo.getString(TITLE);


            String desc="";
            if(productEntry.has(DESC)){
                desc = productEntry.getString(DESC);
            }

            String imgUrl = "";
            if(productEntry.has(IMG_URL_PATH) && productEntry.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = productEntry.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackProject(barcode, title, subtitle, desc, imgUrl);

            if(productEntry.has(AUTHORS)) {
                writeBackAuthors(barcode, productEntry.getJSONArray(AUTHORS));
            }
            if(productEntry.has(CATEGORIES)){
                writeBackCategories(barcode,productEntry.getJSONArray(CATEGORIES) );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

    }

    private void writeBackProject(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(OpenFoodContract.ProductEntry._ID, ean);
        values.put(OpenFoodContract.ProductEntry.TITLE, title);
        values.put(OpenFoodContract.ProductEntry.IMAGE_URL, imgUrl);
        values.put(OpenFoodContract.ProductEntry.SUBTITLE, subtitle);
        values.put(OpenFoodContract.ProductEntry.DESC, desc);
        getContentResolver().insert(OpenFoodContract.ProductEntry.CONTENT_URI,values);
    }

    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(OpenFoodContract.AuthorEntry._ID, ean);
            values.put(OpenFoodContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(OpenFoodContract.AuthorEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(OpenFoodContract.CategoryEntry._ID, ean);
            values.put(OpenFoodContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(OpenFoodContract.CategoryEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    */
    }
}