package com.ereinecke.eatsafe.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ereinecke.eatsafe.R;
import com.ereinecke.eatsafe.util.Constants;

import static com.ereinecke.eatsafe.util.Utility.Logd;

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
    private UploadDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (UploadDialogListener) context;
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.upload_dialog)
                .setPositiveButton(R.string.upload_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Logd(LOG_TAG, "Fire ze missiles!");
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
   @SuppressWarnings("EmptyMethod")
   public interface UploadDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

}