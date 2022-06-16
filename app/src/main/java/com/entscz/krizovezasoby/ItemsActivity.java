package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.entscz.krizovezasoby.util.Requests;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class ItemsActivity extends AppCompatActivity {

    ConstraintLayout addItemChooser;
    FloatingActionButton addItemFab;
    ItemAdapter adapter;

    int bagId;
    JSONArray items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button addItemBtn = findViewById(R.id.addItemBtn);
        ListView itemList = findViewById(R.id.itemList);
        addItemFab = findViewById(R.id.addItemFab);
        FloatingActionButton searchProductFab = findViewById(R.id.searchProductFab);
        FloatingActionButton scanCodeFab = findViewById(R.id.scanCodeFab);
        addItemChooser = findViewById(R.id.addItemChooser);

//        Spinner sortSpinner = findViewById(R.id.sortSpinner);
//
//        sortSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{
//                "Datum", "Název"
//        }));

        addItemChooser.setOnClickListener(v -> {
            addItemChooser.setVisibility(View.GONE);
            addItemFab.setVisibility(View.VISIBLE);
        });

//        itemList.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        addItemFab.setOnClickListener(view -> {
//            Intent scannerIntent = new Intent(ItemsActivity.this, ScannerActivity.class);
//            scannerIntent.putExtra("bagId", bagId);
//            startActivity(scannerIntent);
//            Intent chooserIntent = new Intent(ItemsActivity.this, AddItemChooserActivity.class);
//            chooserIntent.putExtra("bagId", bagId);
////            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(chooserIntent);
//            new AddItemDialog(this).show();
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

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter = new ItemAdapter(this, bagId);
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!adapter.hasItems()) return;
            Intent editItemIntent = new Intent(ItemsActivity.this, EditItemActivity.class);
            editItemIntent.putExtra("bagId", bagId);
            editItemIntent.putExtra("itemId", items.optJSONObject(i).optInt("id"));
            editItemIntent.putExtra("productName", items.optJSONObject(i).optJSONObject("product").optString("shortDesc"));
            if(!items.optJSONObject(i).optJSONObject("product").isNull("imgName")) editItemIntent.putExtra("imgName", items.optJSONObject(i).optJSONObject("product").optString("imgName"));
            editItemIntent.putExtra("count", items.optJSONObject(i).optInt("count"));
            if(!items.optJSONObject(i).isNull("expiration")) editItemIntent.putExtra("expiration", items.optJSONObject(i).optString("expiration"));
            startActivity(editItemIntent);
//            Log.i("ItemsActivity", "clicked item "+i);
        });

//        String bagName = intent.getStringExtra("bagName");

//        setTitle(bagName);

        loadItems();

    }

    public void loadItems(){

        try {

            JSONObject bag = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/bag/getInfo.php?bagId="+bagId).await());

            setTitle(bag.getString("name"));

            items = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/item/getItems.php?bagId="+bagId).await());

            String[] itemNames = new String[items.length()];

            for(int i = 0; i<items.length(); i++){
                itemNames[i] = items.getJSONObject(i).getJSONObject("product").getString("shortDesc")
                        +" x" +items.getJSONObject(i).getInt("count")
                        +" - "+items.getJSONObject(i).getString("expiration");
            }

            adapter.setItems(items);

            invalidateOptionsMenu();

//            adapter.addAll(itemNames);

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
//        menu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(items.length()>0){
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

        if(item.getItemId()==R.id.editBag){
//            Toast.makeText(this, "Clicked edit bag", Toast.LENGTH_SHORT).show();
            Intent bagInfoIntent = new Intent(this, BagInfoActivity.class);
            bagInfoIntent.putExtra("bagId", bagId);
            bagInfoIntent.putExtra("isEmpty", items.length()==0);
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