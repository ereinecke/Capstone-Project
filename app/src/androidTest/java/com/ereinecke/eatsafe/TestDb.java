package com.ereinecke.eatsafe;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.ereinecke.eatsafe.data.DbHelper;
import com.ereinecke.eatsafe.data.OpenFoodContract;

import java.util.Map;
import java.util.Set;


/**
 * Created by ereinecke on 7/23/16.
 */

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public final static  long BARCODE = 737628064502L;
    private final static String PRODUCT_NAME  = "Stir-Fry Rice Noodles";
    private final static String IMG_URL = "http://static.openfoodfacts.org/images/products/073/762/806/4502/front_en.6.200.jpg";
    private final static String THUMB_URL = "http://static.openfoodfacts.org/images/products/073/762/806/4502/front_en.6.100.jpg";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getProductValues();

        long retBarcode = db.insert(OpenFoodContract.ProductEntry.TABLE_NAME, null, values);
        assertEquals(BARCODE, retBarcode);

        String[] columns = {
                OpenFoodContract.ProductEntry._ID,
                OpenFoodContract.ProductEntry.PRODUCT_NAME,
                OpenFoodContract.ProductEntry.IMAGE_URL,
                OpenFoodContract.ProductEntry.THUMB_URL,
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                OpenFoodContract.ProductEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, values);

        /* Replace with tests for Ingredients and Allergens table

        values = getAuthorValues();

        retEan = db.insert(OpenFoodContract.AuthorEntry.TABLE_NAME, null, values);

        columns = new String[]{
                OpenFoodContract.AuthorEntry._ID,
                OpenFoodContract.AuthorEntry.AUTHOR
        };

        cursor = db.query(
                OpenFoodContract.AuthorEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, values);
        // test category table

        values = getCategoryValues();
        retEan = db.insert(OpenFoodContract.CategoryEntry.TABLE_NAME, null, values);

        columns = new String[]{
                OpenFoodContract.CategoryEntry._ID,
                OpenFoodContract.CategoryEntry.CATEGORY
        };

        cursor = db.query(
                OpenFoodContract.CategoryEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, values);

        dbHelper.close();
        */
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set <Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(columnName,idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public static ContentValues getProductValues() {

        final ContentValues values = new ContentValues();
        values.put(OpenFoodContract.ProductEntry._ID, BARCODE);
        values.put(OpenFoodContract.ProductEntry.PRODUCT_NAME, PRODUCT_NAME);
        values.put(OpenFoodContract.ProductEntry.IMAGE_URL, IMG_URL);
        values.put(OpenFoodContract.ProductEntry.THUMB_URL, THUMB_URL);

        return values;
    }

    // TODO: Flesh this out after defining full contract
    /*
    public static ContentValues getIngredientValues() {

        final ContentValues values= new ContentValues();
        values.put(OpenFoodContract.IngredientEntry._ID, barcode);
        values.put(OpenFoodContract.IngredientEntry.INGREDIENT, author);

        return values;
    }

    public static ContentValues getCategoryValues() {

        final ContentValues values= new ContentValues();
        values.put(OpenFoodContract.AllergenEntry._ID, barcode);
        values.put(OpenFoodContract.AllergenEntry.ALLERGEN, category);

        return values;
    }
    */
}