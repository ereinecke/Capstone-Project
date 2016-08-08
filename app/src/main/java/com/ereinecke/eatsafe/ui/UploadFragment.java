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
    private static String currentImageFile;
    private static Uri currentImageUri;
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

        // Button listeners
        // Take a product photo
        rootView.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                takeProductPhotos(0);
                // TODO: progress indicator?
            }
        });

        // Get photos from Gallery
        rootView.findViewById(R.id.gallery_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                pickProductPhotos(Constants.PHOTO_TEST);

            }
        });

        // Upload info to
        rootView.findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                uploadProductInfo();

            }
        });

        return rootView;
    }


    public static void updateImage(String imageFile) {
        currentImageFile = imageFile;

        if (imageFile != null) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile); ;
            uploadImageView.setImageBitmap(imageBitmap);
        }

    }

    public static void updateImageFromGallery(Uri imageUri) {
        currentImageUri = imageUri;

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

    /* TODO: Guide user through taking three photos with appropriate prompts (front, ingredients, nutrition panel.)
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
        public String PhotoRequest(int source, int photo);
    }


}
