package com.ereinecke.eatsafe.util;

import android.net.Uri;

/**
 * Object used to display product photos with labels in UploadFragment
 */

public class UploadPhoto {
    public String photoLabel;
    public Uri    productPhoto;

    public UploadPhoto(String label, Uri photo) {
        this.photoLabel = label;
        this.productPhoto = photo;
    }

    public void   setUploadPhotoLabel(String label) {
        this.photoLabel = label;
    }
    public String getUploadPhotoLabel(int position) {
        return this.photoLabel;
    }
    public void   setUploadPhoto(Uri photo) {
        this.productPhoto = photo;
    }
    public String toString() {
        return "PhotoLabel: " + photoLabel + "; Uri: " + productPhoto + "\n";
    }
}
