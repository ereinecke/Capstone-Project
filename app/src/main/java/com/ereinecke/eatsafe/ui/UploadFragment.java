package com.ereinecke.eatsafe.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;

/**
 * UploadFragment allows the user to take product photos, select product photos from the gallery
 * and upload to OpenFoodFacts.org
 */
public class UploadFragment extends Fragment {

    private static final String LOG_TAG = UploadFragment.class.getSimpleName();
    private Uri photoUri;

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
        final View rootView = inflater.inflate(R.layout.fragment_upload, container, false);

        // Take a product photo
        rootView.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                photoUri = Utility.getOutputMediaFileUri(); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, Constants.CAPTURE_IMAGE_REQUEST);
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

}
