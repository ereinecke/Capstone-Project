package com.ereinecke.eatsafe.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

/**
 * Creates a dialog asking the user if they want to upload new data.
 */

public class UploadDialog  extends DialogFragment {

    private final static String LOG_TAG = UploadDialog.class.getSimpleName();

    public static UploadDialog newInstance(String barcode) {
        UploadDialog dialog = new UploadDialog();
        Bundle args = new Bundle();
        args.putString(Constants.DIALOG_TYPE, Constants.DIALOG_UPLOAD );
        dialog.setArguments(args);
        return dialog;
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.upload_dialog)
                .setPositiveButton(R.string.upload_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Log.d(LOG_TAG, "Fire ze missiles!");
                        mListener.onDialogPositiveClick(UploadDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(UploadDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

   /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it.
    */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

}