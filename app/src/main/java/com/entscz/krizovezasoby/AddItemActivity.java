package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONObject;

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

//        TextView productNameView = findViewById(R.id.productName);
        TextView productTitleLabel = findViewById(R.id.productTitle);
        TextView shortDescLabel = findViewById(R.id.shortDesc);
        Button addItemBtn = findViewById(R.id.addItemBtn);
        ImageView productImage = findViewById(R.id.productImage);
//        EditText countInput = findViewById(R.id.countInput);
//        EditText dateDay = findViewById(R.id.dateDay);
//        EditText dateMonth = findViewById(R.id.dateMonth);
//        EditText dateYear = findViewById(R.id.dateYear);
//        EditText expirationInput = findViewById(R.id.expirationInput);

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
        productId = intent.getIntExtra("productId", -1);
        bagId = intent.getIntExtra("bagId", -1);
//        String productName = intent.getStringExtra("productName");
//        String imgName = null;
//        if(intent.hasExtra("imgName")) imgName = intent.getStringExtra("imgName");
//
//        if(imgName!=null) {
//            Bitmap image = Requests.GET_BITMAP("https://zasoby.nggcv.cz/images/" + imgName).await();
//            productImage.setImageBitmap(image);
//        }
//
//        productNameView.setText(productName);

        try {

            JSONObject product = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/product/getProductById.php?productId="+productId).await());

            String productTitle = product.optString("brand")+" • "+product.optString("type")+
                    (product.isNull("amountValue") ? "" : " • "+product.optInt("amountValue")+" "+product.optString("amountUnit"));

            String itemName = product.optString("shortDesc");

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
//            expirationInput.setText(!item.isNull("expiration") ? item.optString("expiration") : "");

//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            setTitle(itemName);

        } catch(Exception e){
            throw new RuntimeException(e);
        }

        addItemBtn.setOnClickListener(view -> {

            try {

//                if(countInput.getText().toString().length()==0){
//                    throw new RuntimeException("Prosím zadejte počet!");
//                }

//                int count = Integer.parseInt(countInput.getText().toString());
                int count = counter.getValue();
//                String expiration = expirationInput.getText().toString();
//                String expiration = getDate(dateDay.getText().toString(), dateMonth.getText().toString(), dateYear.getText().toString());
                String expiration = dateInput.getValue();

                if(count<1) throw new RuntimeException("Počet musí být větší než 0!");
                if(count>99999) throw new RuntimeException("Počet je příliš velký!");

//                Requests.POST("https://zasoby.nggcv.cz/api/item/addItem.php?bagId="+bagId,
//                        "productId="+productId+"&"+
//                                "count="+count+"&"+
//                                "expiration="+expiration
//                ).await();

                Requests.POST("https://zasoby.nggcv.cz/api/item/addItem.php?bagId="+bagId, new Requests.Params()
                        .add("productId", productId)
                        .add("count", count)
                        .add("expiration", expiration)
                ).await();

                Intent itemsIntent = new Intent(AddItemActivity.this, ItemsActivity.class);
                itemsIntent.putExtra("bagId", bagId);
                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(itemsIntent);
                finish();

            } catch (Exception e){
//                throw new RuntimeException(e);
                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        if(date.compareTo(new Date())<1) throw new RuntimeException("Minimální trvanlivost musí být později než dnes!");

        return new SimpleDateFormat("yyyy-MM-dd").format(date);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            // TODO return true here - everywhere?
        }

        return super.onOptionsItemSelected(item);
    }

}