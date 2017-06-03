package com.ereinecke.eatsafe.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.network.OpenFoodAPIClient;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.IOException;
import java.net.HttpCookie;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Opens a Login dialog box with Register and Lost Password buttons
 * If successful, stores user credentials in SharedPreferences.
 * A Register button launches a WebFragment to the OpenFoodFacts new user page.
 */

public class LoginDialog {

    public static final String LOG_TAG = LoginDialog.class.getSimpleName();
    private static EditText passwordView;
    private static EditText userNameView;
    private static String userName;
    private static String password;

    private static OpenFoodAPIClient apiClient;

    public static void showLoginDialog(final Context c)  {

        LayoutInflater li = LayoutInflater.from(c);
        View prompt = li.inflate(R.layout.dialog_login, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setView(prompt);

        passwordView = (EditText) prompt.findViewById(R.id.password);
        userNameView = (EditText) prompt.findViewById(R.id.user_name);

        /* get credentials */
        SharedPreferences preferences = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            userName = preferences.getString(Constants.USER_NAME, null);
            password = preferences.getString(Constants.PASSWORD, null);
        } else {
            userName = ""; password = "";
        }

        userNameView.setText(userName);
        passwordView.setText(password);

        /* Register button */
        alertDialogBuilder.setCancelable(false)
            .setNeutralButton(c.getString(R.string.register),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(LOG_TAG, "in onRegisterClick()");
                    // TODO: Some kind of toast?
                    // Call up a WebFragment
                    Utility.requestWebView(c.getString(R.string.register_url),
                            c.getString(R.string.off_domain));
                    dialog.dismiss();
                }
            });

        /* Login button */
        alertDialogBuilder.setPositiveButton(c.getString(R.string.login),
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //check for internet connectivity
                    if (!Utility.hasConnectivity(c, false)) {
                        final SuperActivityToast st = new SuperActivityToast(c, Utility.errorStyle());
                        st.setText(c.getString(R.string.error_no_internet));
                        st.show();
                        showLoginDialog(c);

                        return;
                    }

                    String userName = userNameView.getText().toString();
                    String password = passwordView.getText().toString();
                    attemptLogin(c, userName, password, false);

                    dialog.dismiss();
                }
            });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();

        if ( userName == null) userNameView.requestFocus();
        else if ( userName.length()>1 ) passwordView.requestFocus();
    }

    public static boolean attemptLogin(final Context c, final String userName,
                                       final String password, final boolean silent) {
        final LoadToast lt;
        boolean loggedIn = false;

        apiClient = new Retrofit.Builder()
                .baseUrl(Constants.OFF_API_TEST_URL)
                .build()
                .create(OpenFoodAPIClient.class);

        /* Basic password format validation
         * TODO: Confirm that password criteria are current and correct
         */
        lt = new LoadToast(c);

        if (!silent) {
            if (!(password.length() >= 6)) {
                passwordView.setError(c.getString(R.string.error_invalid_password));
                passwordView.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(userName)) {
                userNameView.setError(c.getString(R.string.error_field_required));
                userNameView.requestFocus();
                return false;
            }

            lt.setText("logging in...");
            lt.setBackgroundColor(c.getResources().getColor(R.color.colorAccent));
            lt.setTextColor(c.getResources().getColor(R.color.white));
            lt.show();
        }

        // This generates the POST to the login page and collects cookies, saves to SharedPrefs
        apiClient.signIn(userName, password, "Sign-in").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    if (!silent) {
                        lt.setText(c.getString(R.string.error_no_internet));
                        // lt.error();
                        // Utility.hideKeyboard();
                    }
                    Log.d(LOG_TAG, c.getString(R.string.error_no_internet));
                    return;
                }

                String htmlNotParsed = null;
                try {
                    htmlNotParsed = response.body().string();
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Unable to parse the login response page", e);
                }

                SharedPreferences.Editor prefs = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                        0).edit();

                // Login unsuccessful.
                if (htmlNotParsed == null || htmlNotParsed.contains("Incorrect user name or password.")
                        || htmlNotParsed.contains("See you soon!")) {

                    if (!silent) {
                        lt.error();
                        Toast.makeText(c, c.getString(R.string.error_login), Toast.LENGTH_LONG).show();
                        userNameView.setText("");
                        passwordView.setText("");
                    }
                    Log.d(LOG_TAG, c.getString(R.string.error_invalid_password));

                    // Clear credentials from SharedPrefs
                    prefs.putString(Constants.PASSWORD, "");
                    prefs.apply();

                } else {  // successful login

                    // store the user session id (user_session and user_id)
                    for (HttpCookie httpCookie : HttpCookie.parse(response.headers().get("set-cookie"))) {
                        if (httpCookie.getDomain().equals(".openfoodfacts.org") && httpCookie.getPath().equals("/")) {
                            String[] cookieValues = httpCookie.getValue().split("&");
                            for (int i = 0; i < cookieValues.length; i++) {
                                Log.d(LOG_TAG, "cookieValues[" + i + "]: " + cookieValues[i] +
                                        ": " + cookieValues[i + 1]);
                                prefs.putString(cookieValues[i], cookieValues[++i]);
                            }
                            break;
                        }
                    }

                    if (!silent) {
                        lt.success();
                        Toast.makeText(c, c.getResources().getText(R.string.result_login, userName),
                            Toast.LENGTH_LONG).show();
                    }
                    Log.d(LOG_TAG, "userName: \"" + userName + "\"; password: \"" + password + "\"");
                    // Store credential in SharedPrefs
                    prefs.putString(Constants.USER_NAME, userName);
                    prefs.putString(Constants.PASSWORD, password);
                    prefs.apply();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(c, c.getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                lt.error();
                // Utility.hideKeyboard(c);


            }
        });

        // There must be a better way to tell if we succeeded...
        String pword = "";
        SharedPreferences preferences = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            pword = preferences.getString(Constants.PASSWORD, "");
        }
        if (pword.length() > 0) {
            Log.d(LOG_TAG, "Login successful.");
            return true;
        } else {
            Log.d(LOG_TAG, "Login failed.");
            return false;
        }
    }

    public static void whatever() {

    }

}


