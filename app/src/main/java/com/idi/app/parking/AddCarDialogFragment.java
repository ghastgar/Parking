package com.idi.app.parking;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by pau on 02/01/16.
 */
public class AddCarDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String licensePlate);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;
    static EditText editText;

    public static AddCarDialogFragment newInstance() {
        final AddCarDialogFragment fragment = new AddCarDialogFragment();

        editText = (EditText) getDialog().findViewById(R.id.licensePlateEditText);

        //EditText inputCar = (EditText) alertDialog.findViewById(R.id.licensePlateEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }
        });
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_add_car, null))
                // Add action buttons
                .setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        // TODO: Create ticket and pass it as a 2nd parameter
                        EditText inputCar = (EditText) getDialog().findViewById(R.id.licensePlateEditText);
                        String licensePlate = inputCar.getText().toString();
                        Log.d("Dialog", "Input: " + licensePlate);
                        if (licensePlate.equals("")) {
                            Toast.makeText(getActivity().getApplicationContext(), "Escriu la matrícula del cotxe", Toast.LENGTH_SHORT).show();
                        }
                        else mListener.onDialogPositiveClick(AddCarDialogFragment.this, licensePlate);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddCarDialogFragment.this.getDialog().cancel();
                    }
                });
        /* alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                EditText inputCar = (EditText) getDialog().findViewById(R.id.licensePlateEditText);
                String licensePlate = inputCar.getText().toString();
                if (licensePlate.equals("")) {
                    ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else
                    ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            }
        });*/

        return builder.create();
    }
}