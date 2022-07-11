package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.entscz.krizovezasoby.adapters.ItemAdapter;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.model.ItemManager;
import com.entscz.krizovezasoby.util.Requests;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {

    ConstraintLayout addItemChooser;
    FloatingActionButton addItemFab;
    ItemAdapter adapter;

    int bagId;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView itemList = findViewById(R.id.itemList);
        addItemFab = findViewById(R.id.addItemFab);
        FloatingActionButton searchProductFab = findViewById(R.id.searchProductFab);
        FloatingActionButton scanCodeFab = findViewById(R.id.scanCodeFab);
        addItemChooser = findViewById(R.id.addItemChooser);

        // TODO display, sorting

        Spinner displaySpinner = findViewById(R.id.displaySpinner);
        displaySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{
                "Značka • Typ", "Typ • Značka"
        }));

        Spinner sortSpinner = findViewById(R.id.sortSpinner);
        sortSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{
                "Datum", "Název"
        }));

        displaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position==0) adapter.setDisplay(ItemAdapter.DISPLAY_BRAND_FIRST);
//                else adapter.setDisplay(ItemAdapter.DISPLAY_TYPE_FIRST);
                if(position==0) DataManager.items.setDisplay(ItemManager.DISPLAY_BRAND_FIRST);
                else DataManager.items.setDisplay(ItemManager.DISPLAY_TYPE_FIRST);
                adapter.notifyDataSetInvalidated();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        displaySpinner.setSelection(DataManager.items.getDisplay());

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) DataManager.items.setSort(ItemManager.SORT_DATE);
                else DataManager.items.setSort(ItemManager.SORT_NAME);
                if(items==null) return;
                items = DataManager.items.getItems(bagId);
                adapter.setItems(items);
//                Log.i(getClass().getName(), "sorting by "+position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        sortSpinner.setSelection(DataManager.items.getSort());

        addItemChooser.setOnClickListener(v -> {
            addItemChooser.setVisibility(View.GONE);
            addItemFab.setVisibility(View.VISIBLE);
        });

        // TODO animations?
//        itemList.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        addItemFab.setOnClickListener(view -> {
            addItemChooser.setVisibility(View.VISIBLE);
            addItemFab.setVisibility(View.GONE);
        });

        searchProductFab.setOnClickListener(view -> {
            Intent searchProductIntent = new Intent(ItemsActivity.this, SearchItemActivity.class);
            searchProductIntent.putExtra("bagId", bagId);
            startActivity(searchProductIntent);
            addItemChooser.setVisibility(View.GONE);
            addItemFab.setVisibility(View.VISIBLE);
        });

        scanCodeFab.setOnClickListener(view -> {
            Intent scannerIntent = new Intent(ItemsActivity.this, ScannerActivity.class);
            scannerIntent.putExtra("bagId", bagId);
            startActivity(scannerIntent);
            addItemChooser.setVisibility(View.GONE);
            addItemFab.setVisibility(View.VISIBLE);
        });

        adapter = new ItemAdapter(this, bagId);
        itemList.setAdapter(adapter);

        loadItems();

    }

    public void loadItems(){

        items = null;
        adapter.setItems(null);

        new Thread(()->{
            Looper.prepare();
            try {

                String bagName = DataManager.bags.getBagById(bagId).name;
                runOnUiThread(()->{
                    setTitle(bagName);
                });

                // TODO fetch only if changed?
                DataManager.items.fetchItems(bagId);
                items = DataManager.items.getItems(bagId);
                runOnUiThread(()->{
                    adapter.setItems(items);
                    invalidateOptionsMenu();
                });

            } catch (Requests.NetworkError | DataManager.APIError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(items!=null && items.size()>0){
            menu.findItem(R.id.donateBag).setEnabled(true);
        } else {
            menu.findItem(R.id.donateBag).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            if(addItemChooser.getVisibility()==View.VISIBLE){
                addItemChooser.setVisibility(View.GONE);
                addItemFab.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
            return true;
        }

        if(item.getItemId()==R.id.editBag){
            Intent bagInfoIntent = new Intent(this, BagInfoActivity.class);
            bagInfoIntent.putExtra("bagId", bagId);
            startActivity(bagInfoIntent);
            return true;
        }

        if(item.getItemId()==R.id.donateBag){
            Intent donateBagIntent = new Intent(this, DonateBagActivity.class);
            donateBagIntent.putExtra("bagId", bagId);
            startActivity(donateBagIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(addItemChooser.getVisibility()==View.VISIBLE){
            addItemChooser.setVisibility(View.GONE);
            addItemFab.setVisibility(View.VISIBLE);
            return;
        }

        super.onBackPressed();
    }
}