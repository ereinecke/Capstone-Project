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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.UploadPhoto;
import com.ereinecke.eatsafe.util.UploadPhotosAdapter;

import java.util.ArrayList;

import static com.ereinecke.eatsafe.MainActivity.getBarcodeRequested;

/**
 * UploadFragment allows the user to take product photos, select product photos from the gallery
 * and upload to OpenFoodFacts.org
 */
public class UploadFragment extends Fragment  {

    private static final String LOG_TAG = UploadFragment.class.getSimpleName();
    private static int whichPhoto = 0;
    private PhotoRequest photoRequest;
    private Uri mCurrentPhoto;
    private static View rootView;


    public static ArrayList<UploadPhoto> uploadPhotoArrayList;


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

            String barcodeRequested = getBarcodeRequested();
            if (barcodeRequested == null) barcodeRequested = "";

            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_upload, container, false);

            EditText barcodeView = rootView.findViewById(R.id.barcode);
            if (barcodeRequested.length() > 0) {
                barcodeView.setText(barcodeRequested);
            }

            // Create and populate UploadPhotoArrayList
            uploadPhotoArrayList = new ArrayList<UploadPhoto>();
            uploadPhotoArrayList.add(new UploadPhoto(getString(R.string.product_photo), null));
            uploadPhotoArrayList.add(new UploadPhoto(getString(R.string.ingredients_photo), null));
            uploadPhotoArrayList.add(new UploadPhoto(getString(R.string.nutrition_photo), null));

            // Create the adapter to convert the array to views
            UploadPhotosAdapter adapter = new UploadPhotosAdapter(getContext(), uploadPhotoArrayList);

            // Attach UploadPhotosAdapter to ListView
            ListView listView = rootView.findViewById(R.id.product_photos_listview);
            listView.setAdapter(adapter);

            // Set up unit spinner
            Spinner unitSpinner = rootView.findViewById(R.id.unit_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                    getContext(), R.array.units_array, android.R.layout.simple_spinner_dropdown_item);
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitSpinner.setAdapter(unitAdapter);

            // This may be removed if we go with camera & upload buttons in array adapter
            BottomNavigationView bottomNavigation = rootView.findViewById(R.id.upload_toolbar_bottom);
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

    // Getters and Setters
    public static CharSequence getPhotoLabel(int position) {
        return uploadPhotoArrayList.get(position).photoLabel;
    }

    public static Uri getProductPhoto(int position) {
        return uploadPhotoArrayList.get(position).productPhoto;
    }

    public static void clearProductPhoto(int position) {
        uploadPhotoArrayList.get(position).setUploadPhoto(null);
        return;
    }

    public static int getSelection() {
        return whichPhoto;
    }

    public static void setSelection(int position) {
        whichPhoto =  position;
    }

    public static void updateImage(Uri imageFile) {

        if (imageFile != null) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.toString());
//            uploadImageView.setImageBitmap(imageBitmap);
        }
    }

    // TODO: Here's where we set the image into the ListArray
    public static void updateImageFromGallery(Uri imageUri) {

        if (imageUri != null) {
//            uploadImageView.setImageURI(imageUri);
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
     * temporarily using just one with key PHOTO_TEST.
     */
    private void takeProductPhotos(int photo) {
        mCurrentPhoto = photoRequest.PhotoRequest(Constants.CAMERA_IMAGE_REQUEST, photo);
    }

    public interface PhotoRequest {
        Uri PhotoRequest(int source, int photo);
    }

}
