package com.ereinecke.eatsafe.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DbHelper creates or upgrades the database as needed
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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
                OpenFoodContract.ProductEntry.BRANDS + " TEXT ," +
                OpenFoodContract.ProductEntry.SERVING_SIZE + " TEXT ," +
                OpenFoodContract.ProductEntry.LABELS + " TEXT ," +
                OpenFoodContract.ProductEntry.ALLERGENS + " TEXT ," +
                OpenFoodContract.ProductEntry.INGREDIENTS + " TEXT ," +
                OpenFoodContract.ProductEntry.ORIGINS + " TEXT ," +
                "UNIQUE ("+ OpenFoodContract.ProductEntry._ID +") ON CONFLICT IGNORE)";

        /* TODO: Review the next two statements carefully
        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + OpenFoodContract.IngredientEntry.TABLE_NAME + " ("+
                OpenFoodContract.IngredientEntry._ID + " INTEGER," +
                OpenFoodContract.IngredientEntry.INGREDIENT + " TEXT," +
                " FOREIGN KEY (" + OpenFoodContract.IngredientEntry._ID + ") REFERENCES " +
                OpenFoodContract.ProductEntry.TABLE_NAME + " (" + OpenFoodContract.ProductEntry._ID + "))";

        final String SQL_CREATE_ALLERGEN_TABLE = "CREATE TABLE " + OpenFoodContract.AllergenEntry.TABLE_NAME + " ("+
                OpenFoodContract.AllergenEntry._ID + " INTEGER," +
                OpenFoodContract.AllergenEntry.ALLERGEN + " TEXT," +
                " FOREIGN KEY (" + OpenFoodContract.AllergenEntry._ID + ") REFERENCES " +
                OpenFoodContract.ProductEntry.TABLE_NAME + " (" + OpenFoodContract.ProductEntry._ID + "))";
           */

        Log.d("sql-statements",SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

        /*
        Log.d("sql-statements",SQL_CREATE_INGREDIENT_TABLE);
        Log.d("sql-statements",SQL_CREATE_ALLERGEN_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        db.execSQL(SQL_CREATE_ALLERGEN_TABLE);
        */
    }

    // TODO: Need this!
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            final String ALTER_TBL =
                    "ALTER TABLE " + OpenFoodContract.ProductEntry.TABLE_NAME +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.BRANDS + " TEXT ," +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.SERVING_SIZE + " TEXT ," +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.LABELS + " TEXT ," +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.ALLERGENS + " TEXT ," +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.INGREDIENTS + " TEXT ," +
                    " ADD COLUMN " + OpenFoodContract.ProductEntry.ORIGINS + " TEXT;";
            db.execSQL(ALTER_TBL);
        }
    }
}
