package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class BagsActivity extends AppCompatActivity {

    JSONArray bags;
    BagAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bags);

        setTitle("Tašky");



        EditText newBagInput = findViewById(R.id.newBagInput);
        Button newBagBtn = findViewById(R.id.newBagButton);
        ListView bagList = findViewById(R.id.bagList);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter = new BagAdapter(this);
        bagList.setAdapter(adapter);

        bagList.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!adapter.hasBags()) return;
            Intent intent = new Intent(BagsActivity.this, ItemsActivity.class);
            intent.putExtra("bagId", bags.optJSONObject(i).optInt("id"));
//            intent.putExtra("bagName", bags.optJSONObject(i).optString("name"));
            startActivity(intent);
        });

        newBagBtn.setOnClickListener(view -> {
            try {

                String name = StringUtils.toFirstUpper(newBagInput.getText().toString().trim());

                if(name.length()==0) throw new RuntimeException("Prosím zadejte název!");

                newBagInput.setText("");

//                JSONObject bag = new JSONObject(Requests.POST("https://zasoby.nggcv.cz/api/bag/createBag.php",
//                        "name="+name+
//                                "&description="
//                ).await());

                JSONObject bag = new JSONObject(Requests.POST("https://zasoby.nggcv.cz/api/bag/createBag.php", new Requests.Params()
                        .add("name", name)
                        .add("description", "")
                ).await());

//                recreate();
//                loadBags();

                int bagId = bag.optInt("id");

                Intent bagIntent = new Intent(BagsActivity.this, ItemsActivity.class);
                bagIntent.putExtra("bagId", bagId);
                startActivity(bagIntent);

            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBags();
    }

    void loadBags(){

        try {

            bags = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/bag/listBags.php").await());

//            String[] bagNames = new String[bags.length()];
//
//            for(int i = 0; i<bags.length(); i++){
//                bagNames[i] = bags.getJSONObject(i).getString("name");
//            }

//            adapter.addAll(bagNames);
            adapter.setBags(bags);

        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 0, 0, "Odhlásit se");
        menu.add(0, 1, 1, "Nastavení");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==0){
            SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(loginIntent);
            finish();
            return true;
        }

        if(item.getItemId()==1){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}