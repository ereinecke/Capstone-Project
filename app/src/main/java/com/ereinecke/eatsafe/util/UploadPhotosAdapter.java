package com.ereinecke.eatsafe.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ereinecke.eatsafe.R;

import java.util.ArrayList;

import static com.ereinecke.eatsafe.ui.UploadFragment.clearProductPhoto;
import static com.ereinecke.eatsafe.ui.UploadFragment.getPhotoLabel;
import static com.ereinecke.eatsafe.ui.UploadFragment.getProductPhoto;
import static com.ereinecke.eatsafe.ui.UploadFragment.setSelection;

/**
 * ArrayAdapter to populate the product photos that are to be uploaded.
 */

public class UploadPhotosAdapter extends ArrayAdapter<UploadPhoto> {

    private static CharSequence labelText;

    public UploadPhotosAdapter(Context context, ArrayList<UploadPhoto> uploadPhotos) {
        super(context, 0, uploadPhotos);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Resources resources = getContext().getResources();

        // Get the data item for this position
        UploadPhoto whichPhoto = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.upload_photo, parent, false);
        }
        // Lookup view for data population
        TextView photoLabel = convertView.findViewById(R.id.photo_label);
        photoLabel.setText(getPhotoLabel(position));

        // Get photo imageView reference
        final ImageView productPhoto = convertView.findViewById(R.id.product_photo);

        // Get delete button reference
        final ImageButton delete_button = convertView.findViewById(R.id.delete_button);

        Uri image = getProductPhoto(position);
        if (image != null) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(image.toString());
            productPhoto.setImageBitmap(imageBitmap);
            delete_button.setVisibility(View.VISIBLE);
        } else {  // Hide button if there's no image in adapter
            delete_button.setVisibility(View.INVISIBLE);
        }

        // OnClickListener: shows list item as selected by elevating
        final View listItemView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(position);
                listItemView.setBackgroundColor(resources.getColor(R.color.second_color));
                // TODO: clear previously selected item
            }
        });

        // Delete button OnClickListener: deletes the photo, re-establishes background logo
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearProductPhoto(position);

                // Replace image with background
                productPhoto.setImageResource(R.drawable.eatsafe_logo_text_356x246);
                productPhoto.setImageAlpha(resources.getInteger(R.integer.product_photo_background_alpha));
                delete_button.setVisibility(View.INVISIBLE);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
