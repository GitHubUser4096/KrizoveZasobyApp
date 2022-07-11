package com.entscz.krizovezasoby.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.StringUtils;

public class AddBagDialog {

    public static void show(Activity context){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setNegativeButton("Zrušit", null)
                .setPositiveButton("OK", null)
                .create();

        dialog.setTitle("Přidat tašku");

        View view = context.getLayoutInflater().inflate(R.layout.dialog_add_bag, null);
        dialog.setView(view);

        EditText bagName = view.findViewById(R.id.bagName);

        dialog.show();

        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okBtn.setEnabled(false);

        bagName.requestFocus();
        ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        dialog.setOnDismissListener(dialog1 -> {
            ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        okBtn.setOnClickListener(v -> {

            String name = StringUtils.toFirstUpper(bagName.getText().toString().trim());

            try {

                if(name.length()==0) throw new DataManager.ContentError("Prosím zadejte název!");

                int bagId = DataManager.bags.createBag(name);

                dialog.dismiss();

                Intent bagIntent = new Intent(context, ItemsActivity.class);
                bagIntent.putExtra("bagId", bagId);
                context.startActivity(bagIntent);

            } catch(Requests.NetworkError | DataManager.APIError | DataManager.ContentError e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        bagName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                okBtn.setEnabled(s.length()>0);
            }
        });

    }

}
