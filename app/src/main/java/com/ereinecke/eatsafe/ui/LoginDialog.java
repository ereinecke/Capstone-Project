package com.ereinecke.eatsafe.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;
import com.ereinecke.eatsafe.util.Utility;

/**
 * Opens a Login dialog box with Register and Lost Password buttons
 */

public class LoginDialog {

    public static final String LOG_TAG = LoginDialog.class.getSimpleName();
    private static String userName = "";
    private static String password;

    /* from https://raz-soft.com/android/android-show-login-dialog/ */
    public static void showLoginDialog(Context c, final Bundle credentials)  {

        final Context context = c;
        // if (credentials == null) credentials = new Bundle();

        LayoutInflater li = LayoutInflater.from(context);
        View prompt = li.inflate(R.layout.dialog_login, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(prompt);

        final EditText passwordView = (EditText) prompt.findViewById(R.id.password);
        final EditText userNameView = (EditText) prompt.findViewById(R.id.user_name);

        /* get credentials */
        userName = credentials.getString(Constants.USER_NAME);
        password = credentials.getString(Constants.AUTHENTICATION_TOKEN);
        if (userName != null) {
            userNameView.setText(userName);
        }
        if (password != null) {
            userNameView.setText(password);
        }

        /* Register button */
        alertDialogBuilder.setCancelable(false)
            .setNeutralButton(context.getString(R.string.register),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(LOG_TAG, "in onRegisterClick()");
                    // Call up a WebFragment
                    Utility.requestWebView(context.getString(R.string.register_url),
                            context.getString(R.string.off_domain));
                }
            });

        alertDialogBuilder.setPositiveButton(context.getString(R.string.login),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //check for internet connectivity
                    if (!Utility.hasConnectivity(context, false)) {
                        Toast.makeText(context, "No internet access... please connect.",
                                Toast.LENGTH_LONG).show();
                        showLoginDialog(context, credentials);
                        return;
                    }

                    String password = passwordView.getText().toString();
                    String username = userNameView.getText().toString();

                    // TODO: Login, return credentials and dismiss

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
}
