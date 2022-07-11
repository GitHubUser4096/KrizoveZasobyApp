package com.entscz.krizovezasoby.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.model.Bag;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.ValueError;
import com.entscz.krizovezasoby.views.Counter;

import java.util.ArrayList;

public class MoveItemDialog {

    public static void show(Activity context, int bagId, Item item){
        new MoveItemDialog(context).createDialog(bagId, item);
    }

    private Activity context;
    private int[] bagIds;

    private MoveItemDialog(Activity context){
        this.context = context;
    }

    private void createDialog(int bagId, Item item){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Přesunout")
                .setNegativeButton("Zrušit", null)
                .setPositiveButton("OK", null)
                .create();

        View view = context.getLayoutInflater().inflate(R.layout.dialog_move_item, null);

        Spinner bagSelect = view.findViewById(R.id.bagSelect);
        Counter counter = view.findViewById(R.id.counter);
        counter.setTopValue(item.count);

        try {

            ArrayList<Bag> bags = DataManager.bags.getBags();

            String[] bagNames = new String[bags.size()-1];
            bagIds = new int[bags.size()-1];

            int j = 0;
            for(int i = 0; i<bags.size(); i++){
                int id = bags.get(i).id;
                if(id==bagId) continue;
                bagIds[j] = id;
                bagNames[j] = bags.get(i).name;
                j++;
            }

            bagSelect.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, bagNames));

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        dialog.setView(view);

        dialog.show();

        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        okBtn.setOnClickListener(v -> {

            int moveBagId;
            int count;

            try {

                moveBagId = bagIds[bagSelect.getSelectedItemPosition()];
                count = counter.getValue();

                if(count<1) throw new ValueError("Počet musí být větší než 0!");
                if(count>item.count) throw new ValueError("Počet nesmí být větší než počet položky!");

            } catch(ValueError e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            Item existingItem;
            try {
                existingItem = DataManager.items.findItem(moveBagId, item.product.id, item.expiration, item.used);
            } catch(Requests.NetworkError | DataManager.APIError e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if(existingItem!=null){
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Spojit položky?")
                        .setMessage("Položka již existuje. Spojit položky?")
                        .setNegativeButton("Zrušit", null)
                        .setPositiveButton("OK", (dialog2, which) -> {
                            moveItem(item, moveBagId, count);
                        })
                        .show();
            } else {
                moveItem(item, moveBagId, count);
            }

        });

    }

    private void moveItem(Item item, int moveBagId, int moveCount){

        try {

            // TODO use DataManager?
            Requests.POST(DataManager.API_URL+"/item/moveItem.php?itemId=" + item.id, new Requests.Params()
                    .add("bagId", moveBagId)
                    .add("moveCount", moveCount)
            ).await();

            Intent itemsIntent = new Intent(context, ItemsActivity.class);
            itemsIntent.putExtra("bagId", moveBagId);
            itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(itemsIntent);
            context.finish();

        } catch(Requests.NetworkError e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(Requests.HTTPError e){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show();
        }

    }

}
