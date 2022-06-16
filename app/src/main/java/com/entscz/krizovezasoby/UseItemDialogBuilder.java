package com.entscz.krizovezasoby;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class UseItemDialogBuilder extends AlertDialog.Builder {

    public static interface AcceptListener{
        void onAccepted(int value);
    }

    public UseItemDialogBuilder(@NonNull Activity context, int itemCount, AcceptListener acceptListener) {
        super(context);

        setTitle("Použít");

        View view = context.getLayoutInflater().inflate(R.layout.dialog_use_item, null);

        Counter counter = view.findViewById(R.id.counter);
        counter.setTopValue(itemCount);

//        Button decrementBtn = view.findViewById(R.id.decrementBtn);
//        EditText countInput = view.findViewById(R.id.countInput);
//        Button incrementBtn = view.findViewById(R.id.incrementBtn);

//        decrementBtn.setOnClickListener(v -> {
//            String strVal = countInput.getText().toString();
//            int value;
//            if(strVal.length()==0){
//                value = 0;
//            } else {
//                value = Integer.parseInt(strVal);
//            }
//            if(value>1) countInput.setText(""+(value-1));
//        });
//
//        incrementBtn.setOnClickListener(v -> {
//            String strVal = countInput.getText().toString();
//            int value;
//            if(strVal.length()==0){
//                value = 0;
//            } else {
//                value = Integer.parseInt(strVal);
//            }
//            countInput.setText(""+(value+1));
//        });

        setView(view);
        setNegativeButton("Zrušit", null);
        setPositiveButton("OK", (dialog, which) -> {
//            String strVal = countInput.getText().toString();
//            int value;
//            if(strVal.length()==0){
//                value = 0;
//            } else {
//                value = Integer.parseInt(strVal);
//            }
            acceptListener.onAccepted(counter.getValue());
        });
    }
}
