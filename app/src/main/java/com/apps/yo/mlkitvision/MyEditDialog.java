package com.apps.yo.mlkitvision;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MyEditDialog extends AppCompatDialogFragment {
    EditText num;
    Button btn;
    private MyEditDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState); we will return our own

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editdialog, null);

        builder.setView(view)
                .setTitle("Correct OCR")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {//this is better so you dont have to add btns
                    @Override                                                       //in your own layout XML & less code too
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("CLICK", "New val: " + num.getText().toString());
                        float newVal = Float.valueOf(num.getText().toString());
                        listener.applyChange(newVal);
                    }
                });

        num = view.findViewById(R.id.editText2);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MyEditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface MyEditDialogListener {
        void applyChange(float newVal);
    }

}