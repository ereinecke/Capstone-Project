package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

/**
 * UploadFragment allows the user to take product photos, select product photos from the gallery
 * and upload to OpenFoodFacts.org
 */
public class UploadFragment extends Fragment {

    private static final String LOG_TAG = UploadFragment.class.getSimpleName();
    private PhotoRequest photoRequest;
    private String mCurrentPhoto;
    private static View rootView;
    private static ImageView uploadImageView;


    public UploadFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState == null) {

            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_upload, container, false);
            uploadImageView = (ImageView) rootView.findViewById(R.id.upload_imageView);
        }

        // Bottom toolbar
        AHBottomNavigation bottomToolbar =
                (AHBottomNavigation) rootView.findViewById(R.id.upload_toolbar_bottom);
        AHBottomNavigationItem cameraButton =
                new AHBottomNavigationItem(getString(R.string.camera_button),
                R.drawable.ic_add_a_photo_black_24dp);
        AHBottomNavigationItem galleryButton =
                new AHBottomNavigationItem(getString(R.string.gallery_button),
                R.drawable.ic_delete_black_24dp);
        AHBottomNavigationItem uploadButton =
                new AHBottomNavigationItem(getString(R.string.upload_button),
                R.drawable.ic_cloud_upload_black_24dp);

        // Add items
        bottomToolbar.removeAllItems();
        bottomToolbar.addItem(cameraButton);
        bottomToolbar.addItem(galleryButton);
        bottomToolbar.addItem(uploadButton);

        // Set background color
        bottomToolbar.setDefaultBackgroundColor(getResources().getColor(R.color.colorTabs));

        // Change colors
        bottomToolbar.setAccentColor(getResources().getColor(R.color.colorSelectedButton));
        bottomToolbar.setInactiveColor(getResources().getColor(R.color.colorInactiveButton));

        // Disable the translation inside the CoordinatorLayout
        bottomToolbar.setBehaviorTranslationEnabled(true);

        // Force toolbar to be shown
        bottomToolbar.restoreBottomNavigation(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottomToolbar.setForceTitlesDisplay(false);

        // Force to tint the drawable (useful for font with icon for example)
        bottomToolbar.setForceTint(true);

        // Set listeners
        bottomToolbar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case Constants.UPLOAD_CAMERA_BUTTON:
                        takeProductPhotos(Constants.PHOTO_TEST);
                        break;
                    case Constants.UPLOAD_GALLERY_BUTTON:
                        pickProductPhotos(Constants.PHOTO_TEST);
                        break;
                    case Constants.UPLOAD_UPLOAD_BUTTON:
                        uploadProductInfo();
                        break;
                    default:

                        break;
                }
                return true;
            }
        });
        return rootView;
    }


    public static void updateImage(String imageFile) {
        String currentImageFile = imageFile;

        if (imageFile != null) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile);
            uploadImageView.setImageBitmap(imageBitmap);
        }
    }

    public static void updateImageFromGallery(Uri imageUri) {
        Uri currentImageUri = imageUri;

        if (imageUri != null) {

            uploadImageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        photoRequest = (PhotoRequest) c;
    }

    /* TODO: validate that we have minimum required data and upload to OpenFoodFacts.org */
    private void uploadProductInfo() {
        Snackbar.make(rootView, "Upload not yet implemented", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
    }

    /* TODO: Guide user through selecting three existing photos with appropriate prompts (front, ingredients, nutrition panel.)
     * temporarily using just one with key PHTOTO_TEST.
     */
    private void pickProductPhotos(int photo) {
        mCurrentPhoto = photoRequest.PhotoRequest(Constants.GALLERY_IMAGE_REQUEST, photo);
    }

    /* TODO: Guide user through taking three photos with appropriate prompts (front, ingredients, nutrition panel.)
     * temporarily using just one with key PHTOTO_TEST.
     */
    private void takeProductPhotos(int photo) {
        mCurrentPhoto = photoRequest.PhotoRequest(Constants.CAMERA_IMAGE_REQUEST, photo);
    }

    public interface PhotoRequest {
        String PhotoRequest(int source, int photo);
    }


}
