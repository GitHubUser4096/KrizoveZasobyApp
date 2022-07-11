package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.entscz.krizovezasoby.views.Counter;
import com.entscz.krizovezasoby.views.DateInput;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.ValueError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    int productId;
    int bagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Přidat položku");

        TextView productTitleLabel = findViewById(R.id.productTitle);
        TextView shortDescLabel = findViewById(R.id.shortDesc);
        Button addItemBtn = findViewById(R.id.addItemBtn);
        ImageView productImage = findViewById(R.id.productImage);
        Counter counter = findViewById(R.id.counter);
        DateInput dateInput = findViewById(R.id.dateInput);

        Intent intent = getIntent();
        productId = intent.getIntExtra("productId", -1);
        bagId = intent.getIntExtra("bagId", -1);

        try {

            Product product = DataManager.products.getProductById(productId);

            productTitleLabel.setText(product.getTitle());
            shortDescLabel.setText(product.shortDesc);
            productImage.setBackgroundColor(getColor(R.color.white));
            if(product.imgName!=null) {
                new Thread(()->{
                    Bitmap image = Requests.GET_BITMAP(DataManager.IMG_URL+"/" + product.imgName).await();
                    runOnUiThread(()->{
                        productImage.setImageBitmap(image);
                    });
                }).start();
            } else {
                productImage.setImageResource(android.R.drawable.ic_menu_help);
            }

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        addItemBtn.setOnClickListener(view -> {

            int count;
            String expiration;

            try {
                count = counter.getValue();
                expiration = dateInput.getValue();
                if(count<=0) throw new ValueError("Počet musí být větší než 0!");
                if(count>99) throw new ValueError("Počet je příliš velký!");
                if(expiration.length()>0) {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(expiration);
                    Date now = new Date();
                    if (date.compareTo(now) < 0) {
                        throw new ValueError("Datum musí být později než dnes!");
                    }
                }
            } catch(ValueError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch (ParseException e){
                throw new RuntimeException(e);
            }

            if(DataManager.items.findItem(bagId, productId, expiration, false)!=null){
                new AlertDialog.Builder(this)
                        .setTitle("Spojit položky?")
                        .setMessage("Položka již existuje. Spojit položky?")
                        .setNegativeButton("Zrušit", null)
                        .setPositiveButton("OK", (dialog, which) -> {
                            addItem(count, expiration);
                        })
                        .show();
            } else {
                addItem(count, expiration);
            }

        });

    }

    void addItem(int count, String expiration){

        try {

            // TODO DataManager?
            Requests.POST(DataManager.API_URL + "/item/addItem.php?bagId=" + bagId, new Requests.Params()
                    .add("productId", productId)
                    .add("count", count)
                    .add("expiration", expiration)
            ).await();

            Intent itemsIntent = new Intent(AddItemActivity.this, ItemsActivity.class);
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