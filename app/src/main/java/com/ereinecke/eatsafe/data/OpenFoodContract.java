package com.ereinecke.eatsafe.data;

/**
 * Contract for local OpenFoodFacts database
 */

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class OpenFoodContract {

    public static final String CONTENT_AUTHORITY = "com.ereinecke.eatsafe";
    public static final Uri    BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_ALLERGENS = "allergens";

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PRODUCTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";
        public static final String _ID = "_id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String IMAGE_URL = "img_small_url";
        public static final String THUMB_URL = "img_front_thumb_url";
        public static final String INGREDIENTS_IMG_URL = "img_ingredients_url";
        public static final String NUTRITION_IMG_URL = "img_nutrition_url";
        public static final String BRANDS = "brands";
        public static final String SERVING_SIZE = "servingSize";
        public static final String LABELS = "labels";
        public static final String ALLERGENS = "allergens";
        public static final String INGREDIENTS = "ingredients";
        public static final String ORIGINS = "origins";


        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class IngredientEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        public static final String TABLE_NAME = "ingredients";
        public static final String INGREDIENT = "ingredient";

        public static Uri buildIngredientUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class AllergenEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALLERGENS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ALLERGENS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ALLERGENS;

        public static final String TABLE_NAME = "allergens";
        public static final String ALLERGEN = "allergen";

        public static Uri buildAllergenUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}