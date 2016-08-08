package com.ereinecke.eatsafe.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.data.OpenFoodContract;
import com.ereinecke.eatsafe.services.OpenFoodService;
import com.ereinecke.eatsafe.util.Constants;
import com.hkm.slider.Animations.DescriptionAnimation;
import com.hkm.slider.Indicators.PagerIndicator;
import com.hkm.slider.SliderLayout;
import com.hkm.slider.SliderTypes.AdjustableSlide;
import com.hkm.slider.SliderTypes.BaseSliderView;

import java.util.ArrayList;

/**
 * ProductFragment displays detailed information for a product, whose barcode is passed to
 * ProductFragment as an argument.
 * TODO: add Share button
 */
public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = ProductFragment.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private String barcode;
    private View rootView;
    private ShareActionProvider shareActionProvider;

    public ProductFragment() {
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

        Bundle args = getArguments();

        if (args != null) {
            getLoaderManager().restartLoader(LOADER_ID, args, this);
        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product, container, false);

        // Button listeners
        // Share product
        rootView.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                setShareActionProvider(barcode);
            }
        });

        // Delete product from local database
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                Intent productIntent = new Intent(getActivity(), OpenFoodService.class);
                productIntent.putExtra(Constants.CODE, barcode);
                productIntent.setAction(Constants.ACTION_DELETE_PRODUCT);
                getActivity().startService(productIntent);
                getActivity().getSupportFragmentManager().popBackStack();;
            }
        });

        return rootView;
    }

    /* TODO: expand the text to include more product information */
    private void setShareActionProvider(String barcode) {
        if (shareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + barcode);
            shareActionProvider.setShareIntent(shareIntent);
        }
    }




    // private void restartLoader() {
    //    getLoaderManager().restartLoader(LOADER_ID, null, this);
    // }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        long barcode = args.getLong(Constants.BARCODE_KEY);
        String barcodeStr = Long.toString(barcode);

        return new CursorLoader (
                getActivity(),
                OpenFoodContract.ProductEntry.buildProductUri(barcode),
                null,
                OpenFoodContract.ProductEntry._ID + "=?",
                new String[] {barcodeStr},
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        PagerIndicator pagerIndicator = (PagerIndicator) rootView.findViewById(R.id.custom_indicator);

        // Load images into slider
        ArrayList<String> urlImages = new ArrayList<>();
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.IMAGE_URL)));
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.INGREDIENTS_IMG_URL)));
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.NUTRITION_IMG_URL)));

        ArrayList<AdjustableSlide> list = new ArrayList<>();

        // TODO: If there are no product images, substitute OpenFoodFacts logo
        if (urlImages.size() == 0) {
            Log.d(LOG_TAG, "No images found.");
        } else {
            for (int i = 0; i < urlImages.size(); i++) {
                String imgUrl = urlImages.get(i);
                // Some images may not be present
                if (imgUrl.length() != 0) {
                    AdjustableSlide sliderView = new AdjustableSlide(getContext());
                    sliderView
                            .image(urlImages.get(i))
                            .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
                    list.add(sliderView);
                }
            }
        }

        SliderLayout sliderLayout = (SliderLayout) rootView.findViewById(R.id.results_slider);
        sliderLayout.loadSliderList(list);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setSliderTransformDuration(1000, new LinearOutSlowInInterpolator());
        sliderLayout.setCustomIndicator(pagerIndicator);
        sliderLayout.setDuration(5500);
        sliderLayout.startAutoCycle();

        String productName = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.PRODUCT_NAME));
        ((TextView) rootView.findViewById(R.id.product_name)).setText(productName);

        String brands = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.BRANDS));
        ((TextView) rootView.findViewById(R.id.brands)).setText(prefixLabel(R.string.brands, brands));

        String servingSize = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.SERVING_SIZE));
        ((TextView) rootView.findViewById(R.id.serving_size))
                .setText(prefixLabel(R.string.serving_size, servingSize));

        String labels = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.LABELS));
        ((TextView) rootView.findViewById(R.id.labels))
                .setText(prefixLabel(R.string.labels, labels));

        String allergens = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.ALLERGENS));
        ((TextView) rootView.findViewById(R.id.allergens))
                .setText(prefixLabel(R.string.allergens, allergens));

        String ingredients = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.INGREDIENTS));
        ((TextView) rootView.findViewById(R.id.ingredients))
                .setText(prefixLabel(R.string.ingredients, ingredients));

        String origins = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.ORIGINS));
        ((TextView) rootView.findViewById(R.id.origins))
                .setText(prefixLabel(R.string.origins, origins));

        barcode = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry._ID));
        ((TextView) rootView.findViewById(R.id.code))
                .setText(prefixLabel(R.string.code, barcode));

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /* prefixLabel formats a string, prepending fieldName in bold and concatenating fieldContents
     * Returns a spannable suitable for insertion into a TextView.
     */
    public Spanned prefixLabel(int fieldName, String fieldContents) {

        Spanned result;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml("<b>" + getResources().getString(fieldName) + ": </b>" +
                    fieldContents, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            result = Html.fromHtml("<b>" + getResources().getString(fieldName) + ": </b>" +
                    fieldContents);
        }
        return result;
    }
}
