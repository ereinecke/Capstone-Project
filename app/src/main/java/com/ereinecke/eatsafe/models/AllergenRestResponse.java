package com.ereinecke.eatsafe.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Deserialize nested tags array as List of {@link Allergen}
 * From https://github.com/openfoodfacts/openfoodfacts-androidapp
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllergenRestResponse {
    private final List<Allergen> allergens;

    public AllergenRestResponse(@JsonProperty("tags") final List<Allergen> allergens) {
        this.allergens = allergens;
    }

    public List<Allergen> getAllergens() {
        return allergens;
    }
}
