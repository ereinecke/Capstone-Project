package com.ereinecke.eatsafe.util;

/**
 * Defines constants used by EatSafe.
 */
public class Constants {

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";


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
}
