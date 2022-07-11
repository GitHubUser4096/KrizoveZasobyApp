package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.adapters.SearchItemAdapter;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.Timeout;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class SearchItemActivity extends AppCompatActivity {

    int bagId;
    SearchItemAdapter adapter;
    ArrayList<Product> products;
//    Timeout timeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Najít produkt");

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        EditText searchText = findViewById(R.id.searchText);
        ListView itemSuggestions = findViewById(R.id.itemSuggestions);
        LinearProgressIndicator progress = findViewById(R.id.progress);

        progress.setVisibility(View.GONE);

        adapter = new SearchItemAdapter(this);

        itemSuggestions.setAdapter(adapter);

        itemSuggestions.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!adapter.hasItems()) return;
            Intent addItemIntent = new Intent(SearchItemActivity.this, AddItemActivity.class);
            addItemIntent.putExtra("bagId", bagId);
            addItemIntent.putExtra("productId", products.get(i).id);
            startActivity(addItemIntent);
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

//                if(timeout!=null){
//                    timeout.stop();
//                }

                String text = searchText.getText().toString().trim();

                if(text.length()<3){
                    products = null;
                    adapter.setProducts(null);
                    progress.hide();
                    return;
                }

                progress.show();

//                timeout = Timeout.start(500, ()->{
//                    Looper.prepare();
//                    try {
//
//                        products = DataManager.products.searchProducts(text);
//
//                        runOnUiThread(()->{
//                            adapter.setProducts(products);
//                            progress.hide();
//                        });
//
//                    } catch(Requests.NetworkError | DataManager.APIError e) {
//                        Toast.makeText(SearchItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        runOnUiThread(()->{
//                            products = null;
//                            adapter.setProducts(null);
//                            progress.hide();
//                        });
//                    } catch(Exception e){
//                        Log.e(getClass().getName(), "Failed to search items", e);
//                        runOnUiThread(()->{
//                            products = null;
//                            adapter.setProducts(null);
//                            progress.hide();
//                        });
//                    }
//                    timeout = null;
//                });

                new Thread(()->{

                    Looper.prepare();

                    try {

                        products = DataManager.products.searchProducts(text);

                        runOnUiThread(()->{
                            String cTxt = searchText.getText().toString().trim();
                            if(cTxt.length()<3){
                                products = null;
                                adapter.setProducts(null);
                                progress.hide();
                                return;
                            }
                            adapter.setProducts(products);
                            progress.hide();
                        });

                    } catch(Requests.NetworkError | DataManager.APIError e) {
                        Toast.makeText(SearchItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        runOnUiThread(()->{
                            products = null;
                            adapter.setProducts(null);
                            progress.hide();
                        });
                    }

                }).start();

            }
        });

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