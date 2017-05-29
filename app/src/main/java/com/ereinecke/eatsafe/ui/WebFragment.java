package com.ereinecke.eatsafe.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ereinecke.eatsafe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    private static final String URL_PARAM = "url";
    private WebView webView;

    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment WebFragment.
     */
    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(URL_PARAM, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String url = getArguments().getString(URL_PARAM);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) rootView.findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new offWebClient());

        webView.loadUrl(url);

        return rootView;
    }

    private class offWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failing Url)
    }

}
