package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

public class ItemsActivity extends AppCompatActivity {

    int bagId;
    JSONArray items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button addItemBtn = findViewById(R.id.addItemBtn);
        ListView itemList = findViewById(R.id.itemList);

        addItemBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemsActivity.this, SearchItemActivity.class);
            intent.putExtra("bagId", bagId);
            startActivity(intent);
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ItemAdapter adapter = new ItemAdapter(this);
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!adapter.hasItems()) return;
            Intent intent = new Intent(ItemsActivity.this, EditItemActivity.class);
            intent.putExtra("bagId", bagId);
            intent.putExtra("itemId", items.optJSONObject(i).optInt("id"));
            intent.putExtra("productName", items.optJSONObject(i).optJSONObject("product").optString("shortDesc"));
            if(!items.optJSONObject(i).optJSONObject("product").isNull("imgName")) intent.putExtra("imgName", items.optJSONObject(i).optJSONObject("product").optString("imgName"));
            intent.putExtra("count", items.optJSONObject(i).optInt("count"));
            if(!items.optJSONObject(i).isNull("expiration")) intent.putExtra("expiration", items.optJSONObject(i).optString("expiration"));
            startActivity(intent);
//            Log.i("ItemsActivity", "clicked item "+i);
        });

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);
//        String bagName = intent.getStringExtra("bagName");

//        setTitle(bagName);

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

//            adapter.addAll(itemNames);

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }

        if(item.getItemId()==R.id.editBag){
//            Toast.makeText(this, "Clicked edit bag", Toast.LENGTH_SHORT).show();
            Intent bagInfoIntent = new Intent(this, BagInfoActivity.class);
            bagInfoIntent.putExtra("bagId", bagId);
            startActivity(bagInfoIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}