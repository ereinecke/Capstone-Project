package com.ereinecke.eatsafe.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ereinecke.eatsafe.MainActivity;
import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.data.OpenFoodContract;
import com.ereinecke.eatsafe.util.App;
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
    private BottomNavigationView bottomNavigation;
    private Context mContext;
    private View rootView;
    private ShareActionProvider shareActionProvider;

    public ProductFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = App.getContext();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean showBlankFragment = false;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product, container, false);

        final Bundle args = getArguments();

        if (args != null) {
            showBlankFragment = (args.getLong(Constants.BARCODE_KEY) == Constants.BARCODE_NONE);
            Log.d(LOG_TAG, "showBlankFragment: " + showBlankFragment);
            getLoaderManager().restartLoader(LOADER_ID, args, this);
        }

        if (showBlankFragment) {
            clearProductFragment();
            shareActionProvider = null;
        } else {
            bottomNavigation = (BottomNavigationView) rootView.findViewById(R.id.product_toolbar_bottom);
            // May not be necessary, specified in layout file:
            // bottomNavigation.inflateMenu(R.menu.upload_actions_menu);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_share:
                            Log.d(LOG_TAG, "Pressed share button");
                            setShareActionProvider(barcode);
                            break;
                        case R.id.action_delete:
                            Log.d(LOG_TAG, "Pressed delete button");
                            deleteItem(barcode);
                            Snackbar.make(rootView, getString(R.string.deleted, barcode),
                                    Snackbar.LENGTH_SHORT)
                                    .setAction("Action",null)
                                    .show();
                            break;
                    }
                    return true;
                }
            });
        };
            shareActionProvider = new ShareActionProvider(getActivity());
        return rootView;
    }


     private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
     }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        long barcode;

        /* In case there are no args, for example, on a refresh */
        try {
            barcode = args.getLong(Constants.BARCODE_KEY);
        } catch (Exception e) {
            barcode = Constants.BARCODE_NONE;
            Log.d(LOG_TAG,"No barcode requested, should return nothing.");
        }
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
        // TODO: or collapse slider view
        if (urlImages.size() == 0) {
            Log.d(LOG_TAG, "No images found.");
            // showOFFLogo();
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

    /* In double-pane mode, asks MainActivity to replace this fragment with Splash Fragment.
     * In single-pane mode, pops this ProductFragment off the stack.
     */
    private void clearProductFragment() {

        if (MainActivity.isTablet) {
            requestSplashFragment();
        } else {
            /* We want a back press here to pop the productFragment off the backstack. */
            requestBackPress();
        }
    }

    /* removes the current item from the database */
    private void deleteItem(String barcode) {

        mContext.getContentResolver().delete(
                OpenFoodContract.ProductEntry.CONTENT_URI,
                OpenFoodContract.ProductEntry._ID + "=" + barcode,
                null
        );
        requestResultsFragment();
        clearProductFragment();
        Log.d(LOG_TAG,"Deleting item# " + barcode);
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

    /* Broadcast Intent to MainActivity.MessageReceiver with fragment display request */
    private void requestResultsFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_RESULTS_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* Broadcast Intent to MainActivity.MessageReceiver with fragment display request */
    private void requestSplashFragment() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_SPLASH_FRAGMENT);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    private void requestBackPress() {
        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY, Constants.ACTION_BACK_PRESS);
        LocalBroadcastManager.getInstance(App.getContext())
                .sendBroadcast(messageIntent);
    }

    /* TODO: expand the text to include more product information
     * TODO: not working, now a menu item, review sample code */
    private void setShareActionProvider(String barcode) {
        if (shareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + barcode);
            shareActionProvider.setShareIntent(shareIntent);
        }
    }



}
