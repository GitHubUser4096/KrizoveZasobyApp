package com.entscz.krizovezasoby.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.ValueError;
import com.entscz.krizovezasoby.views.Counter;

public class UseItemDialog {

    public static void show(ItemsActivity context, Item item){
        new UseItemDialog(context).createDialog(item);
    }

    ItemsActivity context;

    private UseItemDialog(ItemsActivity context){
        this.context = context;
    }

    private void createDialog(Item item){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(item.used ? "Přesunout mezi nepoužité" : "Přesunout mezi použité")
                .setNegativeButton("Zrušit", null)
                .setPositiveButton("OK", null)
                .create();

        View view = context.getLayoutInflater().inflate(R.layout.dialog_use_item, null);

        Counter counter = view.findViewById(R.id.counter);
        counter.setTopValue(item.count);

        dialog.setView(view);

        dialog.show();

        Button okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        okBtn.setOnClickListener(v -> {

            try {

                int count = counter.getValue();

                if(count<1) throw new ValueError("Počet musí být větší než 0!");
                if(count>item.count) throw new ValueError("Počet nesmí být větší než počet položky!");
                // TODO use DataManager?
                if (item.used) {
                    Requests.POST(DataManager.API_URL+"/item/setItemUnused.php?itemId=" + item.id, new Requests.Params()
                            .add("unuseCount", count)
                    ).await();
                } else {
                    Requests.POST(DataManager.API_URL+"/item/setItemUsed.php?itemId=" + item.id, new Requests.Params()
                            .add("useCount", count)
                    ).await();
                }
                context.loadItems();
                dialog.dismiss();

            } catch(Requests.HTTPError e){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show();
            } catch(Requests.NetworkError | ValueError e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

}
