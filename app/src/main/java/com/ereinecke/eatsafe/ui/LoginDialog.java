package com.ereinecke.eatsafe.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ereinecke.eatsafe.MainActivity;
import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.network.OpenFoodAPIClient;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import net.steamcrafted.loadtoast.LoadToast;

import java.net.HttpCookie;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.ereinecke.eatsafe.util.Utility.Logd;

/**
 * Opens a Login dialog box with Register and Lost Password buttons
 * If successful, stores user credentials in SharedPreferences.
 * A Register button launches a WebFragment to the OpenFoodFacts new user page.
 */

public class LoginDialog extends DialogFragment {

    private static final String LOG_TAG = LoginDialog.class.getSimpleName();
    private Context c = null;

    private static EditText passwordView;
    private static EditText userNameView;

    private static SuperActivityToast loginToast;
    private static SuperActivityToast errorToast;
    private static SuperActivityToast successToast;

    public static LoginDialog newInstance() {
        LoginDialog dialog = new LoginDialog();
        return dialog;
    }

    // Use this instance of the interface to deliver action events
    static LoginDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) context;
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final View dialogView = view;

        /* Register button  */
        Button registerButton = dialogView.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View dialogView) {
                Logd(LOG_TAG, "in onRegisterClick()");
                // TODO: Some kind of toast
                // Call up a WebFragment
                Utility.requestWebView(c.getString(R.string.register_url),
                        c.getString(R.string.off_domain));
                dismiss();
            }
        });

        /* Login button */
        Button loginButton = dialogView.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View dialogView) {
                    //check for internet connectivity
                    if (Utility.hasConnectivity(c, false)) {
                        final SuperActivityToast st = new SuperActivityToast(c, Utility.errorStyle());
                        st.setText(c.getString(R.string.error_no_internet));
                        st.show();
                        return;
                    }
                    String userName = userNameView.getText().toString();
                    String password = passwordView.getText().toString();
                    attemptLogin(c, userName, password, false);
                    dismiss();
                }
            });

        /* Cancel (not now) button */
        Button cancelButton = dialogView.findViewById(R.id.not_now);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View dialogView)
            {
                dismiss();
            }
        });

        passwordView = view.findViewById(R.id.password);
        userNameView = view.findViewById(R.id.user_name);

        /* get credentials */
        SharedPreferences preferences = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                Context.MODE_PRIVATE);
        String userName;
        String password;
        if (preferences != null) {
            userName = preferences.getString(Constants.USER_NAME, null);
            password = preferences.getString(Constants.PASSWORD, null);
        } else {
            userName = ""; password = "";
        }

        userNameView.setText(userName);
        passwordView.setText(password);

        if ( userName == null) userNameView.requestFocus();
        else if ( userName.length()>1 ) passwordView.requestFocus();


    }

    public void onLoginClick(View view) {
        //check for internet connectivity
        if (Utility.hasConnectivity(c, false)) {
            final SuperActivityToast st = new SuperActivityToast(c, Utility.errorStyle());
            st.setText(c.getString(R.string.error_no_internet));
            st.show();

            return;
        }

        String userName = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        attemptLogin(c, userName, password, false);

        dismiss();
    }


    public void onRegisterClick(View view) {
        Logd(LOG_TAG, "in onRegister");
        // TODO: Some kind of toast
        // Call up a WebFragment
        Utility.requestWebView(c.getString(R.string.register_url),
                c.getString(R.string.off_domain));
        dismiss();
    }

    public void onLoginCancelClick(View view) {
        dismiss();
    }


    public static boolean attemptLogin(final Context c, final String userName,
                                       final String password, final boolean silent) {
        final LoadToast lt;
        final boolean loggedIn = false;

        OpenFoodAPIClient apiClient = new Retrofit.Builder()
                .baseUrl(Constants.OFF_API_TEST_URL)
                .build()
                .create(OpenFoodAPIClient.class);

        loginToast = new SuperActivityToast(c, Utility.infoStyle());
        loginToast.setIndeterminate(true);
        errorToast = new SuperActivityToast(c, Utility.errorStyle());
        successToast = new SuperActivityToast(c, Utility.infoStyle());

        /* Basic password format validation  */
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

            loginToast.setText("Logging in...");
            loginToast.show();

        }

        // This generates the POST to the login page and collects cookies, saves to SharedPrefs
        apiClient.signIn(userName, password, "Sign-in").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    if (!silent) {
                        loginToast.dismiss();
                        errorToast.setText(c.getString(R.string.error_no_internet));
                        errorToast.setDuration(Style.DURATION_MEDIUM);
                        errorToast.show();
                        // Utility.hideKeyboard();
                    }
                    Logd(LOG_TAG, c.getString(R.string.error_no_internet));
                    return;
                }

                String htmlNotParsed = null;
                try {
                    htmlNotParsed = response.body().string();
                } catch (Exception e) {
                    Logd(LOG_TAG, "Unable to parse the login response page, " + e.getMessage());
                }

                SharedPreferences.Editor prefs = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                        0).edit();

                // Login unsuccessful.
                if (htmlNotParsed == null || htmlNotParsed.contains("Incorrect user name or password.")
                        || htmlNotParsed.contains("See you soon!")) {

                    if (!silent) {
                        loginToast.dismiss();
                        errorToast.setText(c.getString(R.string.error_no_internet));
                        errorToast.setDuration(Style.DURATION_MEDIUM);
                        errorToast.setText(c.getString(R.string.error_login));
                        errorToast.show();

                        userNameView.setText("");
                        passwordView.setText("");
                    }

                    // Clear credentials from SharedPrefs
                    prefs.putString(Constants.PASSWORD, "");
                    prefs.apply();

                } else {  // successful login

                    // store the user session id (user_session and user_id)
                    for (HttpCookie httpCookie : HttpCookie.parse(response.headers().get("set-cookie"))) {
                        if (httpCookie.getDomain().equals(".openfoodfacts.org") && httpCookie.getPath().equals("/")) {
                            String[] cookieValues = httpCookie.getValue().split("&");
                            for (int i = 0; i < cookieValues.length; i++) {
                                Logd(LOG_TAG, "cookieValues[" + i + "]: " + cookieValues[i] +
                                        ": " + cookieValues[i + 1]);
                                prefs.putString(cookieValues[i], cookieValues[++i]);
                            }
                            break;
                        }
                    }

                    if (!silent) {
                        loginToast.dismiss();
                        String msg = c.getResources().getString(R.string.result_login, userName);
                        Logd(LOG_TAG, msg);
                        successToast.setText(msg);
                        successToast.show();
                    }
                    Logd(LOG_TAG, "userName: \"" + userName + "\"; password: \"" + password + "\"");
                    // Store credential in SharedPrefs
                    prefs.putString(Constants.USER_NAME, userName);
                    prefs.putString(Constants.PASSWORD, password);
                    prefs.apply();

                    /* Change Log In to Log Out */
                    MainActivity.setLoggedIn(true);
                    if (mListener != null) {
                        mListener.onLogin();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                loginToast.dismiss();
                errorToast.setText(c.getString(R.string.error_no_internet));
                errorToast.show();
                // Utility.hideKeyboard(c);
            }
        });

        loginToast.dismiss();

        // There must be a better way to tell if we succeeded...
        String pword = "";
        SharedPreferences preferences = c.getSharedPreferences(Constants.LOGIN_PREFERENCES,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            pword = preferences.getString(Constants.PASSWORD, "");
        }
        if (pword.length() > 0) {
            Logd(LOG_TAG, "Login successful.");
            return true;
        } else {
            Logd(LOG_TAG, "Login failed.");
            return false;
        }
    }

    public interface LoginDialogListener {
        void onLogin();
    }

}


