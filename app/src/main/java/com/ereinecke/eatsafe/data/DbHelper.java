package com.ereinecke.eatsafe.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ereinecke.eatsafe.util.Utility.Logd;

/**
 * DbHelper creates or upgrades the database as needed
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 3;
    public  static final String DATABASE_NAME = "eatsafe.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // UPLOADED_BY is a local field only, set if this device uploaded the product.  
        // Contains username or empty string
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
                OpenFoodContract.ProductEntry.UPLOADED_BY + " TEXT ," +
                "UNIQUE ("+ OpenFoodContract.ProductEntry._ID +") ON CONFLICT IGNORE)";

        Logd("sql-statement: ",SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " +
                OpenFoodContract.IngredientEntry.TABLE_NAME + " ("+
                OpenFoodContract.IngredientEntry._ID + " INTEGER PRIMARY KEY," +
                OpenFoodContract.IngredientEntry.INGREDIENT_NAME+ " TEXT NOT NULL," +
                OpenFoodContract.IngredientEntry.INGREDIENT_ID + " TEXT ," +
                OpenFoodContract.IngredientEntry.INGREDIENT_RANK + " INTEGER ," +
                OpenFoodContract.IngredientEntry.INGREDIENT_PCT + " TEXT ," +
                "UNIQUE ("+ OpenFoodContract.IngredientEntry._ID +") ON CONFLICT IGNORE)";

        Logd("sql-statement: ",SQL_CREATE_INGREDIENT_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);

        final String SQL_CREATE_ALLERGEN_TABLE = "CREATE TABLE " +
                OpenFoodContract.AllergenEntry.TABLE_NAME + " ("+
                OpenFoodContract.AllergenEntry._ID + " INTEGER PRIMARY KEY," +
                OpenFoodContract.AllergenEntry.ALLERGEN_URL+ " TEXT NOT NULL," +
                OpenFoodContract.AllergenEntry.ALLERGEN_NAME + " TEXT ," +
                OpenFoodContract.AllergenEntry.ALLERGEN_PRODUCTS + " INTEGER ," +
                OpenFoodContract.AllergenEntry.ALLERGEN_ID + " TEXT ," +
                "UNIQUE ("+ OpenFoodContract.AllergenEntry._ID +") ON CONFLICT IGNORE)";

        Logd("sql-statement: ",SQL_CREATE_ALLERGEN_TABLE);
        db.execSQL(SQL_CREATE_ALLERGEN_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            final String ALTER_TBL =
                    "ALTER TABLE " + OpenFoodContract.ProductEntry.TABLE_NAME +
                    " ADD " + OpenFoodContract.ProductEntry.INGREDIENTS_IMG_URL + " TEXT ," +
                    " ADD " + OpenFoodContract.ProductEntry.NUTRITION_IMG_URL + " TEXT;";
            Logd(LOG_TAG, "Upgrading database to version 2 with SQL statement: " +
                    ALTER_TBL);
            db.execSQL(ALTER_TBL);
        }
        if (oldVersion < DATABASE_VERSION) {
            final String ALTER_TBL =
                    "ALTER TABLE "  + OpenFoodContract.ProductEntry.TABLE_NAME +
                            " ADD " + OpenFoodContract.ProductEntry.UPLOADED_BY + " TEXT;";
            Logd(LOG_TAG, "Upgrading database to version " + newVersion + " with SQL statement: " +
                    ALTER_TBL);
            db.execSQL(ALTER_TBL);

            final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " +
                    OpenFoodContract.IngredientEntry.TABLE_NAME + " ("+
                    OpenFoodContract.IngredientEntry._ID + " INTEGER PRIMARY KEY," +
                    OpenFoodContract.IngredientEntry.INGREDIENT_NAME+ " TEXT NOT NULL," +
                    OpenFoodContract.IngredientEntry.INGREDIENT_ID + " TEXT ," +
                    OpenFoodContract.IngredientEntry.INGREDIENT_RANK + " INTEGER ," +
                    OpenFoodContract.IngredientEntry.INGREDIENT_PCT + " TEXT ," +
                    "UNIQUE ("+ OpenFoodContract.IngredientEntry._ID +") ON CONFLICT IGNORE)";

            Logd("sql-statement: ",SQL_CREATE_INGREDIENT_TABLE);
            db.execSQL(SQL_CREATE_INGREDIENT_TABLE);

            final String SQL_CREATE_ALLERGEN_TABLE = "CREATE TABLE " +
                    OpenFoodContract.AllergenEntry.TABLE_NAME + " ("+
                    OpenFoodContract.AllergenEntry._ID + " INTEGER PRIMARY KEY," +
                    OpenFoodContract.AllergenEntry.ALLERGEN_URL+ " TEXT NOT NULL," +
                    OpenFoodContract.AllergenEntry.ALLERGEN_NAME + " TEXT ," +
                    OpenFoodContract.AllergenEntry.ALLERGEN_PRODUCTS + " INTEGER ," +
                    OpenFoodContract.AllergenEntry.ALLERGEN_ID + " TEXT ," +
                    "UNIQUE ("+ OpenFoodContract.AllergenEntry._ID +") ON CONFLICT IGNORE)";

            Logd("sql-statement: ",SQL_CREATE_ALLERGEN_TABLE);
            db.execSQL(SQL_CREATE_ALLERGEN_TABLE);
        }
    }
}
