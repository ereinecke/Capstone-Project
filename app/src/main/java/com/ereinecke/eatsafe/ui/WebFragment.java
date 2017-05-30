package com.ereinecke.eatsafe.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    private static final String LOG_TAG = WebFragment.class.getSimpleName();
    private String domain;
    private WebView webView;


    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url specifying web resource to view.
     * @param domain specifying a domain to limit the WebView to.  If null, WebView not limited.
     * @return A new instance of fragment WebFragment.
     */
    public static WebFragment newInstance(String url, String domain) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PARAM_URL, url);
        if (domain != null) {
            args.putString(Constants.PARAM_DOMAIN, domain);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String url = getArguments().getString(Constants.PARAM_URL);
        domain = getArguments().getString(Constants.PARAM_DOMAIN);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) rootView.findViewById(R.id.web_view);

        // More secure if not needed
        webView.getSettings().setJavaScriptEnabled(false);
        //

        webView.setWebViewClient(new eatSafeWebClient());

        webView.loadUrl(url);

        return rootView;
    }

    private class eatSafeWebClient extends WebViewClient {
        /* Require user to stay in openfoodfacts domain, else launch browser */
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {

            Uri uri;
            try {
                uri = Uri.parse(url);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception parsing uri: " + url);
                return false;
            }
            /* If the new uri ends with the specified domain, allow it. */
            if (domain != null && uri.getHost().endsWith(domain)) {
                return false;
            }
            // Link outside original host should open in a browser tab outside app
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failing_url) {


        };


    }

    public boolean canGoBack() {
        return this.webView != null && this.webView.canGoBack();
    }

    public void goBack() {
        if (this.webView != null) {
            this.webView.goBack();
        }
    }
}
