package com.entscz.krizovezasoby;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;

public class MoveItemDialogBuilder extends AlertDialog.Builder {

    private int[] bagIds;

    public static interface AcceptListener{
        void onAccepted(int bagId, int count);
    }

    public MoveItemDialogBuilder(@NonNull Activity context, int bagId, int itemCount, MoveItemDialogBuilder.AcceptListener acceptListener) {
        super(context);

        setTitle("Přesunout");

        View view = context.getLayoutInflater().inflate(R.layout.dialog_move_item, null);

        Spinner bagSelect = view.findViewById(R.id.bagSelect);
        Counter counter = view.findViewById(R.id.counter);
        counter.setTopValue(itemCount);

        try {

            JSONArray bags = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/bag/listBags.php").await());

            String[] bagNames = new String[bags.length()-1];
            bagIds = new int[bags.length()-1];

            int j = 0;
            for(int i = 0; i<bags.length(); i++){
                int id = bags.optJSONObject(i).optInt("id");
                if(id==bagId) continue;
                bagNames[j] = bags.optJSONObject(i).optString("name");
                bagIds[j] = id;
                j++;
            }

            bagSelect.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, bagNames));

        } catch(Exception e){
            throw new RuntimeException(e);
        }

//        Button decrementBtn = view.findViewById(R.id.decrementBtn);
//        EditText countInput = view.findViewById(R.id.countInput);
//        Button incrementBtn = view.findViewById(R.id.incrementBtn);
//
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
            int moveBagId = bagIds[bagSelect.getSelectedItemPosition()];
            int count = counter.getValue();
            acceptListener.onAccepted(moveBagId, count);
        });
    }

}
