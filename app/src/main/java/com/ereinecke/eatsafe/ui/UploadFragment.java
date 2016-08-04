package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private static ImageView imageView;


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

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        // Take a product photo
        rootView.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                takeProductPhotos(0);
                // TODO: progress indicator?

            }
        });

        rootView.findViewById(R.id.gallery_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {

            }
        });

        rootView.findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {

            }
        });

        return rootView;
    }


    public static void updateImage(String imageFile) {
        Log.d(LOG_TAG, "Image saved to: " + imageFile);

        Snackbar.make(rootView, "Image saved to: " + imageFile,
                Snackbar.LENGTH_LONG).setAction("Action", null).show();

        if (imageFile != null) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile); ;
            imageView.setImageBitmap(imageBitmap);
        }

    }


    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        photoRequest = (PhotoRequest) c;
    }

    /* TODO: Guide user through taking three photos with appropriate prompts (front, ingredients, nutrition panel.)
     * temporarily using just one with key PHTOTOTEST.
     */
    private void takeProductPhotos(int photo) {
        mCurrentPhoto = photoRequest.PhotoRequest(Constants.PHOTO_TEST);
    }

    public interface PhotoRequest {
        public String PhotoRequest(int photo);
    }
}
