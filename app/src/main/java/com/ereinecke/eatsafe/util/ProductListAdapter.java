package com.ereinecke.eatsafe.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.data.OpenFoodContract;

/**
 * List adapter to display list of products downloaded to this device
 */

public class ProductListAdapter extends CursorAdapter {

    public static class ViewHolder {
        public ImageView productImage = null;
        public TextView productName = null;
        public TextView productBrand = null;
        public TextView productCode = null;

        public ViewHolder(View view) {
            productImage = (ImageView) view.findViewById(R.id.product_image);
            productName = (TextView) view.findViewById(R.id.product_name);
            productBrand = (TextView) view.findViewById(R.id.product_brand);
            productCode = (TextView) view.findViewById(R.id.code);
        }
    }

    public ProductListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(OpenFoodContract.ProductEntry.IMAGE_URL));
        // TODO: get image download working
        // new DownloadImage(viewHolder.productImage).execute(imgUrl);

        String productName = cursor.getString(cursor.getColumnIndex(OpenFoodContract.ProductEntry.PRODUCT_NAME));
        viewHolder.productName.setText(productName);

        String productBrand = cursor.getString(cursor.getColumnIndex(OpenFoodContract.ProductEntry.BRANDS));
        viewHolder.productBrand.setText(productBrand);

        String productCode = cursor.getString(cursor.getColumnIndex(OpenFoodContract.ProductEntry._ID));
        viewHolder.productCode.setText(productCode);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}
