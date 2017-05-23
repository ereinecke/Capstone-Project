package com.ereinecke.eatsafe.util;

/**
 * Defines constants used by EatSafe.
 */
public class Constants {

    // Setting for test ads
    public static final boolean TEST_ADS = true;

    // TODO: Change write URLs to openfoodfacts.org when out of test
    public static final String PRODUCTS_BASE_URL = "http://world.openfoodfacts.org/api/v0/product/";

    // ACTIONs.
    public static final String ACTION_SCAN_BARCODE   = "com.ereinecke.eatsafe.FETCH_SCAN";
    public static final String ACTION_FETCH_PRODUCT  = "com.ereinecke.eatsafe.services.action.FETCH_PRODUCT";
    public static final String ACTION_DELETE_PRODUCT = "com.ereinecke.eatsafe.services.action.DELETE_PRODUCT";
    public static final String ACTION_UPLOAD = "com.ereinecke.eatsafe.ACTION_UPLOAD";

    // Camera & gallery constants
    public static final int CAMERA_IMAGE_REQUEST = 100;
    public static final int GALLERY_IMAGE_REQUEST = 200;
    public static final String IMG_PREFIX = "ES_IMG_";
    public static final String WHICH_PHOTO = "which_photo";
    public static final int PHOTO_TEST = 0;
    public static final int PHOTO_FRONT = 1;
    public static final int PHOTO_INGREDIENT = 2;
    public static final int PHOTO_NUTRITION = 3;
    public static final int PHOTO_OTHER = 4;

    // Scanner constants
    public static final String BARCODE_KEY = "barcode_key";
    public static final long BARCODE_NOT_FOUND = -1L;
    public static final long BARCODE_NONE = 0L;

    // keys for savedInstanceStates
    public static final String CURRENT_PHOTO = "current_photo";

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

    // Define a custom Intent action
    public static final String MESSAGE_EVENT = "message_event";
    public static final String MESSAGE_KEY = "message_key";
    public static final String RESULT_KEY = "result_key";

    // Bottom navigation button positions
    public static final int UPLOAD_CAMERA_BUTTON  = 0;
    public static final int UPLOAD_GALLERY_BUTTON = 1;
    public static final int UPLOAD_UPLOAD_BUTTON  = 2;
    public static final int PRODUCT_SHARE_BUTTON  = 0;
    public static final int PRODUCT_DELETE_BUTTON = 1;
}
