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

public class MyDialog extends AppCompatDialogFragment {
    EditText num;
    Button btn;
    private MyDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState); we will return our own
        //first get arguments
        Bundle mArgs = getArguments();
        final float amt1 = mArgs.getFloat("amt");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        builder.setView(view)
                .setTitle("For Splitting " + amt1);

                /*.setPositiveButton("ok", new DialogInterface.OnClickListener() {//this is better so you dont have to add btns
                    @Override                                                       //in your own layout XML & less code too
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = editTextUsername.getText().toString();
                        String password = editTextPassword.getText().toString();
                        listener.applyTexts(username, password);
                    }
                });*/

        num = view.findViewById(R.id.editText);
        btn = view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICK", "SplitBy: " + num.getText());
                listener.sendDiv(amt1/2);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MyDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface MyDialogListener {
        void sendDiv(float dividedAmt);
    }

}
