package com.entscz.krizovezasoby.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.entscz.krizovezasoby.views.Counter;
import com.entscz.krizovezasoby.views.DateInput;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.ValueError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditItemActivity extends AppCompatActivity {

    int bagId;
    int itemId;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upravit položku");

        TextView productTitleLabel = findViewById(R.id.productTitle);
        TextView shortDescLabel = findViewById(R.id.shortDesc);
        ImageView productImage = findViewById(R.id.productImage);
        Button saveBtn = findViewById(R.id.saveBtn);
        Counter counter = findViewById(R.id.counter);
        DateInput dateInput = findViewById(R.id.dateInput);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);
        itemId = intent.getIntExtra("itemId", -1);

        item = DataManager.items.getItemById(itemId);

        productTitleLabel.setText(item.product.getTitle());
        shortDescLabel.setText(item.product.shortDesc);
        productImage.setBackgroundColor(getColor(R.color.white));
        if(item.product.imgName!=null) {
            Bitmap image = Requests.GET_BITMAP(DataManager.IMG_URL+"/" + item.product.imgName).await();
            productImage.setImageBitmap(image);
        } else {
            productImage.setImageResource(android.R.drawable.ic_menu_help);
        }
        counter.setValue(item.count);
        if(item.expiration!=null){
            dateInput.setValue(item.expiration);
        }

        saveBtn.setOnClickListener(view -> {

            int count;
            String expiration;

            try {
                count = counter.getValue();
                expiration = dateInput.getValue();
                if(count<=0) throw new ValueError("Počet musí být větší než 0!");
                if(count>99) throw new ValueError("Počet je příliš velký!");
            } catch(ValueError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            Item sameItem = DataManager.items.findItem(bagId, item.product.id, expiration, item.used);
            if(sameItem!=null && sameItem.id!=item.id){
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Spojit položky?")
                        .setMessage("Položka již existuje. Spojit položky?")
                        .setNegativeButton("Zrušit", null)
                        .setPositiveButton("OK", (dialog, which) -> {
                            editItem(count, expiration);
                        })
                        .show();
            } else {
                editItem(count, expiration);
            }

        });

    }

    void editItem(int count, String expiration){
        try {

            // TODO DataManager
            Requests.POST(DataManager.API_URL + "/item/editItem.php?itemId=" + itemId, new Requests.Params()
                    .add("count", count)
                    .add("expiration", expiration)
            ).await();

            Intent itemsIntent = new Intent(EditItemActivity.this, ItemsActivity.class);
            itemsIntent.putExtra("bagId", bagId);
            itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(itemsIntent);
            finish();

        } catch(Requests.NetworkError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(Requests.HTTPError e){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}