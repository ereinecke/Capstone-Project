package com.ereinecke.eatsafe.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.data.OpenFoodContract;
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

        boolean showBlankFragment = false;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product, container, false);

        Bundle args = getArguments();

        if (args != null) {
            showBlankFragment = (args.getLong(Constants.BARCODE_KEY) == Constants.BARCODE_NONE);
            Log.d(LOG_TAG, "showBlankFragment: " + showBlankFragment);
            getLoaderManager().restartLoader(LOADER_ID, args, this);
        }

        if (showBlankFragment) {
            clearProductFragment(true);
            shareActionProvider = null;
        } else {

            // Bottom toolbar
            AHBottomNavigation bottomToolbar =
                    (AHBottomNavigation) rootView.findViewById(R.id.product_toolbar_bottom);
            AHBottomNavigationItem shareButton = new AHBottomNavigationItem(R.string.share,
                    R.drawable.ic_share_black_24dp, R.color.letterwhite);
            AHBottomNavigationItem deleteButton = new AHBottomNavigationItem(R.string.delete,
                    R.drawable.ic_delete_black_24dp, R.color.letterwhite);

            // Add items
            bottomToolbar.removeAllItems();
            bottomToolbar.addItem(shareButton);
            bottomToolbar.addItem(deleteButton);

            // Set background color
            bottomToolbar.setDefaultBackgroundColor(getResources().getColor(R.color.colorTabs));

            // Change colors
            bottomToolbar.setAccentColor(getResources().getColor(R.color.colorSelectedButton));
            bottomToolbar.setInactiveColor(getResources().getColor(R.color.colorInactiveButton));

            // Disable the translation inside the CoordinatorLayout
            bottomToolbar.setBehaviorTranslationEnabled(false);

            // Force toolbar to be shown
            bottomToolbar.restoreBottomNavigation(true);

            // Force the titles to be displayed (against Material Design guidelines!)
            bottomToolbar.setForceTitlesDisplay(true);

            // Force to tint the drawable (useful for font with icon for example)
            bottomToolbar.setForceTint(true);

            // Set listeners
            bottomToolbar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                @Override
                public boolean onTabSelected(int position, boolean wasSelected) {
                    switch (position) {
                        case Constants.PRODUCT_SHARE_BUTTON:
                            Log.d(LOG_TAG, "Pressed share button.");
                            setShareActionProvider(barcode);
                            break;
                        case Constants.PRODUCT_DELETE_BUTTON:
                            Log.d(LOG_TAG, "Pressed delete button.");
                            Snackbar.make(rootView, "Product delete not yet implemented",
                                    Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            break;
                        default:
                            Log.d(LOG_TAG, "Unexpected button pressed in bottom navigation.");
                            break;
                    }
                    return true;
                }
            });

            shareActionProvider = new ShareActionProvider(getActivity());
        }

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

        return new CursorLoader(
                getActivity(),
                OpenFoodContract.ProductEntry.buildProductUri(barcode),
                null,
                OpenFoodContract.ProductEntry._ID + "=?",
                new String[]{barcodeStr},
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        barcode = data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry._ID));
        ((TextView) rootView.findViewById(R.id.code))
                .setText(prefixLabel(R.string.code, barcode));
        Log.d(LOG_TAG, "onLoadFinished, barcode: " + barcode);

        PagerIndicator pagerIndicator = (PagerIndicator) rootView.findViewById(R.id.custom_indicator);

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

        // Load images into slider
        ArrayList<String> urlImages = new ArrayList<>();
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.IMAGE_URL)));
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.INGREDIENTS_IMG_URL)));
        urlImages.add(data.getString(data.getColumnIndex(OpenFoodContract.ProductEntry.NUTRITION_IMG_URL)));

        ArrayList<AdjustableSlide> list = new ArrayList<>();

        // TODO: If there are no product images, substitute OpenFoodFacts logo
        if (urlImages.size() == 0) {
            Log.d(LOG_TAG, "No images found.");
            showOFFLogo();
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
        sliderLayout.setDuration(R.integer.slider_delay);
        sliderLayout.startAutoCycle();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /* Clears text fields in ProductFragment.  if offLogo == true, the OpenFoodFacts logo will be
     * placed into the slider
     */
    private void clearProductFragment(boolean offLogo) {

        ((TextView) rootView.findViewById(R.id.code)).setText(" ");
        ((TextView) rootView.findViewById(R.id.product_name)).setText(" ");
        ((TextView) rootView.findViewById(R.id.brands)).setText(" ");
        ((TextView) rootView.findViewById(R.id.serving_size)).setText(" ");
        ((TextView) rootView.findViewById(R.id.labels)).setText(" ");
        ((TextView) rootView.findViewById(R.id.allergens)).setText(" ");
        ((TextView) rootView.findViewById(R.id.ingredients)).setText(" ");
        ((TextView) rootView.findViewById(R.id.origins)).setText(" ");
        if (offLogo) {
            showOFFLogo();
        }
    }

    /* Sets slider to show OpenFoodFacts logo */
    private void showOFFLogo() {

        // TODO: this causes the slider to crash - temporarily disabling
        if (false) {
            // Load drawables into slider by their R.drawable. references
            ArrayList<Integer> logoImages = new ArrayList<>();
            logoImages.add(R.drawable.openfoodfacts_logo_356);

            ArrayList<AdjustableSlide> list = new ArrayList<>();

            if (logoImages.size() == 0) {
                Log.d(LOG_TAG, "No images found in showOFFLogo().");

            } else {
                for (int i = 0; i < logoImages.size(); i++) {
                    Integer logo = logoImages.get(i);

                    // Some images may not be present
                    if (logo != null) {
                        Log.d(LOG_TAG, "Calling image #" + logo);
                        AdjustableSlide sliderView = new AdjustableSlide(getContext());
                        sliderView
                                .image(logoImages.get(i))
                                .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
                        list.add(sliderView);
                        Log.d(LOG_TAG, sliderView.toString());
                    }
                }
            }

            PagerIndicator pagerIndicator = (PagerIndicator) rootView.findViewById(R.id.custom_indicator);

            SliderLayout sliderLayout = (SliderLayout) rootView.findViewById(R.id.results_slider);
            sliderLayout.loadSliderList(list);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setSliderTransformDuration(1000, new LinearOutSlowInInterpolator());
            sliderLayout.setCustomIndicator(pagerIndicator);
            sliderLayout.setDuration(5500);
            sliderLayout.startAutoCycle();
        }
    }

    /* prefixLabel formats a string, prepending fieldName in bold and concatenating fieldContents
     * Returns a spannable suitable for insertion into a TextView.
     */
    public Spanned prefixLabel(int fieldName, String fieldContents) {

        Spanned result;

        /* Needed for API 24 */
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
