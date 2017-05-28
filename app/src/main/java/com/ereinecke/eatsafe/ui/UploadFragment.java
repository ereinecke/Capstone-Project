package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
            BottomNavigationView bottomNavigation = (BottomNavigationView) rootView.findViewById(R.id.upload_toolbar_bottom);
            // May not be necessary, specified in layout file:
            // bottomNavigation.inflateMenu(R.menu.upload_actions_menu);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_camera:
                            takeProductPhotos(Constants.PHOTO_TEST);
                            break;
                        case R.id.action_gallery:
                            pickProductPhotos(Constants.PHOTO_TEST);
                            break;
                        case R.id.action_upload:
                            uploadProductInfo();
                            break;
                    }
                    return true;
                }
                });
            }

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
     * temporarily using just one with key PHOTO_TEST.
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
