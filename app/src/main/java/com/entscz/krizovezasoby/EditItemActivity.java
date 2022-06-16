package com.entscz.krizovezasoby;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.entscz.krizovezasoby.util.Requests;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class EditItemActivity extends AppCompatActivity {

    int bagId;
    int itemId;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upravit položku");

        ConstraintLayout layout = findViewById(R.id.root);

//        TextView productNameView = findViewById(R.id.productName);
        TextView productTitleLabel = findViewById(R.id.productTitle);
        TextView shortDescLabel = findViewById(R.id.shortDesc);
        ImageView productImage = findViewById(R.id.productImage);
//        EditText countInput = findViewById(R.id.countInput);
//        EditText expirationInput = findViewById(R.id.expirationInput);
//        EditText dateDay = findViewById(R.id.dateDay);
//        EditText dateMonth = findViewById(R.id.dateMonth);
//        EditText dateYear = findViewById(R.id.dateYear);
//        ImageButton deleteBtn = findViewById(R.id.deleteBtn);
        Button saveBtn = findViewById(R.id.saveBtn);

        Counter counter = findViewById(R.id.counter);
        DateInput dateInput = findViewById(R.id.dateInput);

//        dateDay.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length()==2){
//                    dateMonth.requestFocus();
//                    dateMonth.selectAll();
//                }
//            }
//        });
//
//        dateMonth.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length()==2){
//                    dateYear.requestFocus();
//                    dateYear.selectAll();
//                }
//            }
//        });

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);
        itemId = intent.getIntExtra("itemId", -1);
//        itemName = intent.getStringExtra("productName");
//        String imgName = null;
//        if(intent.hasExtra("imgName")) imgName = intent.getStringExtra("imgName");
//        int count = intent.getIntExtra("count", 1);
//        String expiration = null;
//        if(intent.hasExtra("expiration")) expiration = intent.getStringExtra("expiration");

        try {

            JSONObject item = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/item/getItemById.php?itemId="+itemId).await());
            JSONObject product = item.optJSONObject("product");

            String productTitle = product.optString("brand")+" • "+product.optString("type")+
                    (product.isNull("amountValue") ? "" : " • "+product.optInt("amountValue")+" "+product.optString("amountUnit"));

            itemName = product.optString("shortDesc");

            productTitleLabel.setText(productTitle);
//            productTitleLabel.setTooltipText(productTitle);
            shortDescLabel.setText(product.optString("shortDesc"));
//            shortDescLabel.setTooltipText(product.optString("shortDesc"));
            if(!product.isNull("imgName")) {
                Bitmap image = Requests.GET_BITMAP("https://zasoby.nggcv.cz/images/" + product.optString("imgName")).await();
                productImage.setImageBitmap(image);
            } else {
                productImage.setImageResource(android.R.drawable.ic_menu_help);
            }
//            countInput.setText(""+item.optString("count"));
            counter.setValue(item.optInt("count"));
//            expirationInput.setText(!item.isNull("expiration") ? item.optString("expiration") : "");
            if(!item.isNull("expiration")){
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(item.optString("expiration"));
//                dateDay.setText(""+date.getDate());
//                dateMonth.setText(""+(date.getMonth()+1));
//                dateYear.setText(""+(date.getYear()+1900));
                dateInput.setValue(date.getDate(), date.getMonth()+1, date.getYear()+1900);

            }

//            setTitle(itemName);

        } catch(Exception e){
            throw new RuntimeException(e);
        }

//        productNameView.setText(itemName);
//        if(imgName!=null) {
//            Bitmap image = Requests.GET_BITMAP("https://zasoby.nggcv.cz/images/" + imgName).await();
//            productImage.setImageBitmap(image);
//        }
//        countInput.setText(""+count);
//        expirationInput.setText(expiration!=null ? expiration : "");
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle(itemName);

//        deleteBtn.setOnClickListener(view -> {
//
//            try {
//
//                Requests.POST("https://zasoby.nggcv.cz/api/item/deleteItem.php?itemId="+itemId, "").await();
//
//                Intent itemsIntent = new Intent(EditItemActivity.this, ItemsActivity.class);
//                itemsIntent.putExtra("bagId", bagId);
//                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(itemsIntent);
////                finish();
//
//            } catch(Exception e){
//                throw new RuntimeException();
//            }
//
//        });

        saveBtn.setOnClickListener(view -> {

            try {

//                if(countInput.getText().toString().length()==0){
//                    throw new RuntimeException("Prosím zadejte počet!");
//                }

//                int newCount = Integer.parseInt(countInput.getText().toString());
                int newCount = counter.getValue();
//                String newExpiration = expirationInput.getText().toString();
//                String newExpiration = getDate(dateDay.getText().toString(), dateMonth.getText().toString(), dateYear.getText().toString());
                String newExpiration = dateInput.getValue();

                if(newCount<1) throw new RuntimeException("Počet musí být větší než 0!");
                if(newCount>99999) throw new RuntimeException("Počet je příliš velký!");

//                Requests.POST("https://zasoby.nggcv.cz/api/item/editItem.php?itemId="+itemId,
//                                "count="+newCount+
//                                "&expiration="+newExpiration
//                ).await();

                Requests.POST("https://zasoby.nggcv.cz/api/item/editItem.php?itemId="+itemId, new Requests.Params()
                        .add("count", newCount)
                        .add("expiration", newExpiration)
                ).await();

                Intent itemsIntent = new Intent(EditItemActivity.this, ItemsActivity.class);
                itemsIntent.putExtra("bagId", bagId);
                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(itemsIntent);
                finish();

            } catch (Exception e){
//                throw new RuntimeException(e);
                Toast.makeText(EditItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                Snackbar.make(layout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

        });

    }

    String getDate(String day, String month, String year){

        if(day.length()==0 && month.length()==0 && year.length()==0) return "";

        if(month.length()==0 || year.length()==0) throw new RuntimeException("Prosím zadejte alespoň měsíc a rok!");

        int iday = 0, imonth, iyear;

        try {
            if(day.length()>0) iday = Integer.parseInt(day);
            imonth = Integer.parseInt(month);
            iyear = Integer.parseInt(year);
        } catch(Exception e){
            throw new RuntimeException("Minimální trvanlivost: prosím zadejte číslo!");
        }

        if(iyear<2000 || iyear>=3000) throw new RuntimeException("Minimální trvanlivost: rok mimo rozsah!");
        if(imonth<1 || imonth>12) throw new RuntimeException("Minimální trvanlivost: měsíc mimo rozsah!");
        if(day.length()>0 && (iday<1 || iday>31)) throw new RuntimeException("Minimální trvanlivost: den mimo rozsah!");

        Date date;
        try {
            if (day.length() == 0) {
                date = new Date(iyear-1900, imonth-1+1, 0);
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + (imonth+1) + "-0");
            } else {
                date = new Date(iyear-1900, imonth-1, iday);
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + imonth + "-" + iday);
            }
        } catch(Exception e){
            throw new RuntimeException("Neočekávaná chyba!");
        }

//        Log.i("zasoby", new SimpleDateFormat("yyyy-MM-dd").format(date));

        if((date.getMonth()+1)!=imonth) throw new RuntimeException("Minimální trvanlivost: den mimo rozsah měsíce!");
//        throw new RuntimeException("test");

        return new SimpleDateFormat("yyyy-MM-dd").format(date);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }

        if(item.getItemId()==R.id.deleteItem){

            new AlertDialog.Builder(this)
                    .setTitle(itemName)
                    .setMessage("Odstranit položku?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> {

                        try {

//                            Requests.POST("https://zasoby.nggcv.cz/api/item/deleteItem.php?itemId="+itemId, "").await();
                            Requests.POST("https://zasoby.nggcv.cz/api/item/deleteItem.php?itemId="+itemId, new Requests.Params()).await();

                            Intent itemsIntent = new Intent(EditItemActivity.this, ItemsActivity.class);
                            itemsIntent.putExtra("bagId", bagId);
                            itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(itemsIntent);
                            finish();

                        } catch(Exception e){
                            throw new RuntimeException(e.getMessage());
                        }

                    })
                    .setNegativeButton("Zrušit", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

}