package com.ereinecke.eatsafe;

/**
 * ContentProvider tests
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.ereinecke.eatsafe.data.OpenFoodContract;


/**
 * 
 */
@SuppressWarnings("deprecation")
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void setUp() {
        deleteAllRecords();
    }

    private void deleteAllRecords() {
        mContext.getContentResolver().delete(
                OpenFoodContract.ProductEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                OpenFoodContract.IngredientEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                OpenFoodContract.AllergenEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                OpenFoodContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assert cursor != null;
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                OpenFoodContract.IngredientEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assert cursor != null;
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                OpenFoodContract.AllergenEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assert cursor != null;
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(OpenFoodContract.ProductEntry.CONTENT_URI);
        assertEquals(OpenFoodContract.ProductEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(OpenFoodContract.IngredientEntry.CONTENT_URI);
        assertEquals(OpenFoodContract.IngredientEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(OpenFoodContract.AllergenEntry.CONTENT_URI);
        assertEquals(OpenFoodContract.AllergenEntry.CONTENT_TYPE, type);

        long id = 9780137903955L;
        type = mContext.getContentResolver().getType(OpenFoodContract.ProductEntry.buildProductUri(id));
        assertEquals(OpenFoodContract.ProductEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(OpenFoodContract.IngredientEntry.buildIngredientUri(id));
        assertEquals(OpenFoodContract.ProductEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(OpenFoodContract.AllergenEntry.buildAllergenUri(id));
        assertEquals(OpenFoodContract.IngredientEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertProduct() {

        insertProduct();

        readProduct();
    }

    private void insertProduct() {
        ContentValues productValues = TestDb.getProductValues();

        Uri productUri = mContext.getContentResolver().insert(OpenFoodContract.ProductEntry.CONTENT_URI, productValues);
        long productRowId = ContentUris.parseId(productUri);
        assertTrue(productRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                OpenFoodContract.ProductEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, productValues);

        cursor = mContext.getContentResolver().query(
                OpenFoodContract.ProductEntry.buildProductUri(productRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, productValues);

    }

    private void readProduct() {

        Cursor cursor = mContext.getContentResolver().query(
                OpenFoodContract.ProductEntry.buildProductUri(TestDb.BARCODE),
                null, // projection
                null, // selection
                null, // selection args
                null  // sort order
        );

        TestDb.validateCursor(cursor, TestDb.getProductValues());
    }
}