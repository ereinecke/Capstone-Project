package com.ereinecke.eatsafe.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DbHelper creates or upgrades the database as needed
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 2;
    public  static final String DATABASE_NAME = "eatsafe.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + OpenFoodContract.ProductEntry.TABLE_NAME + " ("+
                OpenFoodContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                OpenFoodContract.ProductEntry.PRODUCT_NAME+ " TEXT NOT NULL," +
                OpenFoodContract.ProductEntry.IMAGE_URL + " TEXT ," +
                OpenFoodContract.ProductEntry.THUMB_URL + " TEXT ," +
                OpenFoodContract.ProductEntry.INGREDIENTS_IMG_URL + " TEXT ," +
                OpenFoodContract.ProductEntry.NUTRITION_IMG_URL + " TEXT ," +
                OpenFoodContract.ProductEntry.BRANDS + " TEXT ," +
                OpenFoodContract.ProductEntry.SERVING_SIZE + " TEXT ," +
                OpenFoodContract.ProductEntry.LABELS + " TEXT ," +
                OpenFoodContract.ProductEntry.ALLERGENS + " TEXT ," +
                OpenFoodContract.ProductEntry.INGREDIENTS + " TEXT ," +
                OpenFoodContract.ProductEntry.ORIGINS + " TEXT ," +
                "UNIQUE ("+ OpenFoodContract.ProductEntry._ID +") ON CONFLICT IGNORE)";

        Log.d("sql-statements",SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            final String ALTER_TBL =
                    "ALTER TABLE " + OpenFoodContract.ProductEntry.TABLE_NAME +
                    " ADD " + OpenFoodContract.ProductEntry.INGREDIENTS_IMG_URL + " TEXT ," +
                    " ADD " + OpenFoodContract.ProductEntry.NUTRITION_IMG_URL + " TEXT;";
            Log.d(LOG_TAG, "Upgrading database to version " + newVersion + " with SQL statement: " +
                    ALTER_TBL);
            db.execSQL(ALTER_TBL);
        }
    }
}
