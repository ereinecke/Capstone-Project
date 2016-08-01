package com.ereinecke.eatsafe.util;

/**
 * Defines constants used by EatSafe.
 */
public class Constants {

    // TODO: Change write URLs to openfoodfacts.org when out of test
    public static final String PRODUCTS_BASE_URL = "http://world.openfoodfacts.org/api/v0/product/";

    public static final String ACTION_SCAN_BARCODE = "com.ereinecke.eatsafe.services.extra.barcode";
    public static final String ACTION_FETCH_PRODUCT = "com.ereinecke.eatsafe.services.action.FETCH_PRODUCT";
    public static final String ACTION_DELETE_PRODUCT = "com.ereinecke.eatsafe.services.action.DELETE_PRODUCT";

    public static final int CAPTURE_BARCODE_REQUEST = 100;
    public static final int CAPTURE_IMAGE_REQUEST   = 200;

    public static final String BARCODE_KEY = "barcode_key";

    // JSON keys
    public static final String STATUS = "status";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_NAME = "product_name";
    public static final String CODE = "code";
    public static final String IMG_URL = "image_small_url";
    public static final String THUMB_URL ="image_thumb_url";
    public static final String INGREDIENTS_IMG_URL = "image_ingredients_small_url";
    public static final String NUTRITION_IMG_URL = "image_nutrition_small_url";
    public static final String BRANDS = "brands";
    public static final String SERVING_SIZE = "serving_size";
    public static final String LABELS = "labels";
    public static final String ALLERGENS = "allergens";
    public static final String INGREDIENTS = "ingredients_text";
    public static final String ORIGINS = "origins";

    // Defines a custom Intent action
    public static final String MESSAGE_EVENT = "message_event";
    public static final String MESSAGE_KEY = "message_key";
    public static final String RESULT_KEY = "result_key";
}
