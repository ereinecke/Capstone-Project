package com.ereinecke.eatsafe.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.ereinecke.eatsafe.util.Utility.Logd;

/**
 * ContentProvider for EatSafe
 */

public class OpenFoodProvider extends ContentProvider {

    private static final String LOG_TAG = OpenFoodProvider.class.getSimpleName();

    private static final int PRODUCT_ID = 100;
    private static final int PRODUCT = 101;

    private static final int INGREDIENT_ID = 200;
    private static final int INGREDIENT = 201;

    private static final int ALLERGEN_ID = 300;
    private static final int ALLERGEN = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static final SQLiteQueryBuilder productFull;

    // TODO: Add ingredients and allergens
    static {
        productFull = new SQLiteQueryBuilder();
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = OpenFoodContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, OpenFoodContract.PATH_PRODUCTS, PRODUCT);
        matcher.addURI(authority, OpenFoodContract.PATH_PRODUCTS+"/#", PRODUCT);

        matcher.addURI(authority, OpenFoodContract.PATH_INGREDIENTS, INGREDIENT);
        matcher.addURI(authority, OpenFoodContract.PATH_INGREDIENTS+"/#", INGREDIENT);

        matcher.addURI(authority, OpenFoodContract.PATH_ALLERGENS, ALLERGEN);
        matcher.addURI(authority, OpenFoodContract.PATH_ALLERGENS+"/#", ALLERGEN);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case PRODUCT:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PRODUCT_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.ProductEntry.TABLE_NAME,
                        projection,
                        OpenFoodContract.ProductEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

                case INGREDIENT:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case INGREDIENT_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.IngredientEntry.TABLE_NAME,
                        projection,
                        OpenFoodContract.IngredientEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

                case ALLERGEN:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.AllergenEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ALLERGEN_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        OpenFoodContract.AllergenEntry.TABLE_NAME,
                        projection,
                        OpenFoodContract.AllergenEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch(NullPointerException e) {
            Logd(LOG_TAG, e.getMessage());
        }

        return retCursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCT_ID:
                return OpenFoodContract.ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return OpenFoodContract.ProductEntry.CONTENT_TYPE;
            case INGREDIENT_ID:
                return OpenFoodContract.ProductEntry.CONTENT_ITEM_TYPE;
            case INGREDIENT:
                return OpenFoodContract.ProductEntry.CONTENT_TYPE;
            case ALLERGEN_ID:
                return OpenFoodContract.ProductEntry.CONTENT_ITEM_TYPE;
            case ALLERGEN:
                return OpenFoodContract.ProductEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case PRODUCT: {
                long _id = db.insert(OpenFoodContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 ){
                    returnUri = OpenFoodContract.ProductEntry.buildProductUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                try {
                    getContext().getContentResolver().notifyChange(OpenFoodContract.ProductEntry
                            .buildProductUri(_id), null);
                } catch(NullPointerException e) {
                    Logd(LOG_TAG, e.getMessage());
                }
                break;
            }
            case INGREDIENT: {
                // TODO: implement inserting ingredients
                Logd(LOG_TAG, "Inserting ingredient not yet implemented.");
            }
            case ALLERGEN: {
                // TODO: implement inserting allergens
                Logd(LOG_TAG, "Inserting allergens not yet implemented.");
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String sqlString;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Logd(LOG_TAG, "match: " + match + "; uri: " + uri.toString());

        int rowsDeleted;
        switch (match) {
            // Delete product table
            case PRODUCT:
                rowsDeleted = db.delete(
                        OpenFoodContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete specified product
            case PRODUCT_ID:
                sqlString = OpenFoodContract.ProductEntry._ID + " = '" +
                        ContentUris.parseId(uri) + "'";
                Logd(LOG_TAG, sqlString);
                rowsDeleted = db.delete(
                        OpenFoodContract.ProductEntry.TABLE_NAME,
                        sqlString,
                        selectionArgs);
                break;
            // Delete ingredient table
            case INGREDIENT:
                rowsDeleted = db.delete(
                        OpenFoodContract.IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete specified ingredient
            case INGREDIENT_ID:
                sqlString = OpenFoodContract.IngredientEntry._ID + " = '" +
                        ContentUris.parseId(uri) + "'";
                Logd(LOG_TAG, sqlString);
                rowsDeleted = db.delete(
                        OpenFoodContract.IngredientEntry.TABLE_NAME,
                        sqlString,
                        selectionArgs);
                break;
            // Delete allergen table
            case ALLERGEN:
                rowsDeleted = db.delete(
                        OpenFoodContract.AllergenEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete specified allergen
            case ALLERGEN_ID:
                sqlString = OpenFoodContract.AllergenEntry._ID + " = '" +
                        ContentUris.parseId(uri) + "'";
                Logd(LOG_TAG, sqlString);
                rowsDeleted = db.delete(
                        OpenFoodContract.AllergenEntry.TABLE_NAME,
                        sqlString,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Logd(LOG_TAG, e.getMessage());
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case PRODUCT:
                rowsUpdated = db.update(OpenFoodContract.ProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Logd(LOG_TAG, e.getMessage());
            }
        }
        return rowsUpdated;
    }
}