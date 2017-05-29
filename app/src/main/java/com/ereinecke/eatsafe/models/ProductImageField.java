package com.ereinecke.eatsafe.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerates product image types
 * From https://github.com/openfoodfacts/openfoodfacts-androidapp
 */

public enum ProductImageField {
    FRONT, INGREDIENTS, NUTRITION, OTHER;

    @Override
    @JsonValue
    public String toString() {
        return this.name().toLowerCase();
    }
}
