package com.ereinecke.eatsafe.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.data.OpenFoodContract;
import com.ereinecke.eatsafe.util.ProductListAdapter;
import com.ereinecke.eatsafe.util.Utility;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * ResultsFragment shows all products stored in ContentReceiver.  In future, there will be a
 * search function to return specific items.
 *
 * TODO: Add search (product_name || brand)
 */
public class ResultsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ResultsFragment.class.getSimpleName();
    private ProductListAdapter productListAdapter;
    private EditText searchText;
    private Cursor cursor;
    private ListView productList;

    public ResultsFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        cursor = getActivity().getContentResolver().query(
                OpenFoodContract.ProductEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (cursor == null) {
            Log.d(LOG_TAG, "Cursor returned 0 results.");
        } else {
            Log.d(LOG_TAG, "Cursor returned " + cursor.getCount() + " results.");
        }

        productList = rootView.findViewById(R.id.product_list);
        productListAdapter = new ProductListAdapter(getActivity(), cursor, 0);

        /* TODO: Add search (product_name || brand) */
        searchText = rootView.findViewById(R.id.searchText);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Snackbar.make(rootView, "@string/history search_not_enabled",
                        Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        );

        /* TODO: add an EditText handler to trigger local history search */

        ListView productList = rootView.findViewById(R.id.product_list);
        productList.setAdapter(productListAdapter);

        /* Ad displayed in bottom toolbar, respecting Constants.TEST_ADS flag   */
        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = Utility.getAdRequest();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        } else {
            Log.d(LOG_TAG, "adView not found");
        }

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = productListAdapter.getCursor();
                Log.d(LOG_TAG, "Item # " + l + " selected.");
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Utility.Callback) getActivity())
                            .onItemSelected(cursor.getString(cursor
                                    .getColumnIndex(OpenFoodContract.ProductEntry._ID)));
                }
            }
        });

        return rootView;
    }

    public void restartLoader() {
        int LOADER_ID = 10;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = OpenFoodContract.ProductEntry.PRODUCT_NAME + " LIKE ? OR " +
                OpenFoodContract.ProductEntry.BRANDS + " LIKE ? ";
        String searchString = searchText.getText().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    OpenFoodContract.ProductEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                OpenFoodContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        productListAdapter.swapCursor(data);
        //noinspection ConstantConditions
        int position = ListView.INVALID_POSITION;
        if (position != ListView.INVALID_POSITION) {
            productList.smoothScrollToPosition(position);
        }
        // Cursor needs to be closed to prevent leaks
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productListAdapter.swapCursor(null);
    }
}

