package com.ereinecke.eatsafe.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ereinecke.eatsafe.data.OpenFoodContract;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
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
                // returns true if product found - not sure if I want to do anything with that here
                fetchProduct(barcode);
            }
        }
    }

    /**
     * Handle action fetchProduct in the provided background thread with the provided
     * parameters.  Returns:
     *      true if product found, either in ContentProvider or downloaded
     *          from OpenFoodFacts.org.
     *      false if product not found for whatever reason.
     */
    private boolean fetchProduct(String barcode) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String productJsonString = null;

        if (!Utility.validateBarcode(barcode)) {
            return false;
        }

        /* Get from local database if present */
        Cursor productEntry = getContentResolver().query(
                OpenFoodContract.ProductEntry.buildProductUri(Long.parseLong(barcode)),
                null, // leaving "columns" null just returns all the columns.
                OpenFoodContract.ProductEntry._ID + "=?", // cols for "where" clause
                new String[] {barcode}, // values for "where" clause
                null  // sort order
        );

        assert productEntry != null;
        if(productEntry.getCount() > 0) {
            Log.d(LOG_TAG, "Product " + barcode + " already downloaded");
            returnResult("Product already downloaded.", Long.parseLong(barcode));
            productEntry.close();
            return true;
        }

        productEntry.close();

        /* pull down JSON */
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
                return false;
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                Log.d(LOG_TAG, "Input buffer null");
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return false;
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

        /*  JSON parsing happens here */
        try {
            JSONObject productJson = new JSONObject(productJsonString);
            JSONObject productObject;

            if(productJson.has(Constants.STATUS)) {
                if (productJson.getInt(Constants.STATUS) == 1)
                    Log.d(LOG_TAG, "Product status: found");
                    productObject = productJson.getJSONObject(Constants.PRODUCT);
            } else {
                // Returns result
                returnResult("Product not found", -1L);
                return false;
            }

            // TODO:  Add more items
            long productId = productJson.getLong(Constants.CODE);
            String productName = productObject.getString(Constants.PRODUCT_NAME);
            String imgUrl =  productObject.getString(Constants.IMG_URL);
            String thumbUrl = productObject.getString(Constants.THUMB_URL);
            String brands = productObject.getString(Constants.BRANDS);
            String labels = productObject.getString(Constants.LABELS);
            String servingSize = productObject.getString(Constants.SERVING_SIZE);
            String allergens = productObject.getString(Constants.ALLERGENS);
            String ingredients = getIngredients(productObject);
            String origins = productObject.getString(Constants.ORIGINS);

            writeProduct(productId, productName, imgUrl, thumbUrl, brands, labels, servingSize,
                    allergens, ingredients, origins);

            // Broadcast result
            returnResult("Product found", productId);

        } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
        }
        return true;
    }

    private void returnResult(String message, long productId) {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, message);
        messageIntent.putExtra(Constants.RESULT_KEY, productId);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(messageIntent);
    }

    private void writeProduct(long productId, String productName, String imgUrl,
                              String thumbUrl, String brands, String labels, String servingSize,
                              String allergens, String ingredients, String origins) {
        ContentValues values= new ContentValues();
        values.put(OpenFoodContract.ProductEntry._ID, productId);
        values.put(OpenFoodContract.ProductEntry.PRODUCT_NAME, productName);
        values.put(OpenFoodContract.ProductEntry.IMAGE_URL, imgUrl);
        values.put(OpenFoodContract.ProductEntry.THUMB_URL, thumbUrl);
        values.put(OpenFoodContract.ProductEntry.BRANDS, brands);
        values.put(OpenFoodContract.ProductEntry.LABELS, labels);
        values.put(OpenFoodContract.ProductEntry.SERVING_SIZE, servingSize);
        values.put(OpenFoodContract.ProductEntry.ALLERGENS, allergens);
        values.put(OpenFoodContract.ProductEntry.INGREDIENTS, ingredients);
        values.put(OpenFoodContract.ProductEntry.ORIGINS, origins);

        Log.d(LOG_TAG, "writeBackProject: values=" + values.toString());
        getContentResolver().insert(OpenFoodContract.ProductEntry.CONTENT_URI,values);
    }

    /*
     * getIngredients tries to handle the multilingual versions of product ingredients
     * Currently will try to get english, but will fall back to native language
     * TODO: decode and use "languages_tags"
     */
    private String getIngredients(JSONObject product) {
        String languageCode = "en";
        String ingredients = "";

        try {
            ingredients = product.getString(Constants.INGREDIENTS + "_" + languageCode);
        } catch(JSONException e) {
            try {
                ingredients = product.getString(Constants.INGREDIENTS);
            } catch(JSONException j) {
                Log.d(LOG_TAG, "Ingredients not found.");
            }
        }
        return ingredients;
    }
}