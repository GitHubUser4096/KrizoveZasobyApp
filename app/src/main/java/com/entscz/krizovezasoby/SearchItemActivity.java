package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;

public class SearchItemActivity extends AppCompatActivity {

    int bagId;
    JSONArray products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Najít produkt");

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        EditText searchText = findViewById(R.id.searchText);
//        Button scanBtn = findViewById(R.id.scanCodeBtn);
//        Button addProductBtn = findViewById(R.id.addProductBtn);
        ListView itemSuggestions = findViewById(R.id.itemSuggestions);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        itemSuggestions.setAdapter(adapter);

        itemSuggestions.setOnItemClickListener((adapterView, view, i, l) -> {
            try {
                Intent addItemIntent = new Intent(SearchItemActivity.this, AddItemActivity.class);
                addItemIntent.putExtra("bagId", bagId);
                addItemIntent.putExtra("productName", adapter.getItem(i));
                addItemIntent.putExtra("productId", products.getJSONObject(i).getInt("id"));
                if(!products.getJSONObject(i).isNull("imgName")) addItemIntent.putExtra("imgName", products.getJSONObject(i).getString("imgName"));
                startActivity(addItemIntent);
            } catch(Exception e){
                e.printStackTrace();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text = searchText.getText().toString().trim();

                if(text.length()==0){
                    adapter.clear();
                    return;
                }

                try {

                    products = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/product/searchProducts.php?search="+text).await());

                    String[] productNames = new String[products.length()];

                    for(int i = 0; i<products.length(); i++){
                        productNames[i] = products.getJSONObject(i).getString("shortDesc");
                    }

                    adapter.clear();
                    adapter.addAll(productNames);

                } catch (Exception e){
                    throw new RuntimeException(e);
                }

            }
        });

//        scanBtn.setOnClickListener(view -> {
//
//            Intent scannerIntent = new Intent(SearchItemActivity.this, ScannerActivity.class);
//            scannerIntent.putExtra("bagId", bagId);
//            startActivity(scannerIntent);
//
//        });

//        addProductBtn.setOnClickListener(view -> {
//
//            Intent addProductIntent = new Intent(SearchItemActivity.this, AddProductActivity.class);
//            addProductIntent.putExtra("bagId", bagId);
//            startActivity(addProductIntent);
//
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

        if(item.getItemId()==R.id.addProduct){
            Intent addProductIntent = new Intent(this, AddProductActivity.class);
            addProductIntent.putExtra("bagId", bagId);
            startActivity(addProductIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}