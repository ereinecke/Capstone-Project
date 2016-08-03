package com.ereinecke.eatsafe.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    /*
    private void launchPhotoIntent(int whichPhoto) {
        Log.d(LOG_TAG, "Launching intent for photo #" + whichPhoto);
        // create Intent to take a picture and return control to the calling application
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = openOutputMediaFile();
            Log.d(LOG_TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath);

            if (photoFile != null) {

                Uri photoUri = StreamProvider
                        .getUriForFile("com.ereinecke.eatsafe.fileprovider", photoFile);
                Log.d(LOG_TAG, "photoUri: " + photoUri.toString());
                // set the image file name
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // start the image capture Intent
                getActivity().startActivityForResult(takePictureIntent,
                        Constants.CAPTURE_IMAGE_REQUEST);
            }
        }
    }
    */

    /** Returns a unique, opened file for image; sets mCurrentPhotoPath with filespec */
    /*
    public File openOutputMediaFile(){

        String appName = App.getContext().getString(R.string.app_name);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        Log.d(LOG_TAG, "Environment.DIRECTORY_PICTURES: " + Environment.DIRECTORY_PICTURES);
        Log.d(LOG_TAG, "Environment.getExternalStoragePublicDirectory: " +
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        Log.d(LOG_TAG, "context.getExternalFilesDir(Environment.DIRECTORY_PICTURES): " +
                        getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        // Photos will be stored in a subdirectory (app_name) under the photos directory
        File mediaStorageDir = new File(getContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), appName);
        Log.d(LOG_TAG, "mediaStorageDir: " + mediaStorageDir.toString());

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "failed to create directory " + mediaStorageDir);
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_"+ timeStamp;
        File imageFile = null;
        Log.d(LOG_TAG, "Image file name: " + fileName);
        try {
            imageFile = File.createTempFile(fileName, ".jpg", mediaStorageDir);
            Log.d(LOG_TAG, "imageFile: " + imageFile);
        } catch(IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }

        // Generate a file: path for use with intent
        if (imageFile != null) {
            mCurrentPhotoPath = "file:" + imageFile.getAbsolutePath();
        }
        return imageFile;
    }
    */
}
