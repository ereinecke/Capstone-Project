package com.ereinecke.eatsafe.util;

/**
 * Defines constants used by EatSafe.
 */
public class Constants {

    // TODO: Change write URLs to openfoodfacts.org when out of test
    public static final String PRODUCTS_BASE_URL = "http://world.openfoodfacts.org/api/v0/product/";

    public static final String BARCODE = "com.ereinecke.eatsafe.services.extra.barcode";
    public static final String FETCH_PRODUCT = "com.ereinecke.eatsafe.services.action.FETCH_PRODUCT";

    // JSON keys
    public static final String STATUS = "status";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_NAME = "product_name";
    public static final String CODE = "code";
    public static final String IMG_URL = "image_small_url";
    public static final String IMG_THUMB_URL = "image_front_thumb_url";

    // Defines a custom Intent action
    public static final String MESSAGE_EVENT = "message_event";
    public static final String MESSAGE_KEY = "message_extra";
    public static final String RESULT_KEY = "result_key";
}
