package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.entscz.krizovezasoby.dialogs.AddBagDialog;
import com.entscz.krizovezasoby.adapters.BagAdapter;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.Bag;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BagsActivity extends AppCompatActivity {

    BagAdapter adapter;
    ArrayList<Bag> bags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bags);

        setTitle("Krizové zásoby");

//        EditText newBagInput = findViewById(R.id.newBagInput);
//        Button newBagBtn = findViewById(R.id.newBagButton);
        ListView bagList = findViewById(R.id.bagList);
        FloatingActionButton addBagFab = findViewById(R.id.addBagFab);
//        ProgressBar progress = findViewById(R.id.progress);

        adapter = new BagAdapter(this);
        bagList.setAdapter(adapter);

        bagList.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!adapter.hasBags()) return;
            Intent bagIntent = new Intent(BagsActivity.this, ItemsActivity.class);
//            intent.putExtra("bagId", bags.optJSONObject(i).optInt("id"));
            bagIntent.putExtra("bagId", bags.get(i).id);
//            intent.putExtra("bagName", bags.optJSONObject(i).optString("name"));
            startActivity(bagIntent);
        });

        addBagFab.setOnClickListener(v -> {
            AddBagDialog.show(this);
        });

//        newBagBtn.setOnClickListener(view -> {
//
//            String name = StringUtils.toFirstUpper(newBagInput.getText().toString().trim());
//            newBagInput.setText("");
//
//            new Thread(){
//                @Override
//                public void run() {
//
//                    Looper.prepare();
//
//                    try {
//
//                        if(name.length()==0) throw new DataManager.ContentError("Prosím zadejte název!");
//
//                        runOnUiThread(new Thread(){
//                            @Override
//                            public void run() {
//                                newBagBtn.setVisibility(View.INVISIBLE);
//                                progress.setVisibility(View.VISIBLE);
//                            }
//                        });
//
////                        int bagId = DataManager.get().createBag(name);
//                        int bagId = DataManager.bags.createBag(name);
//
//                        Intent bagIntent = new Intent(BagsActivity.this, ItemsActivity.class);
//                        bagIntent.putExtra("bagId", bagId);
//                        startActivity(bagIntent);
//
//                        runOnUiThread(new Thread(){
//                            @Override
//                            public void run() {
//                                newBagBtn.setVisibility(View.VISIBLE);
//                                progress.setVisibility(View.GONE);
//                            }
//                        });
//
//                    } catch(Requests.NetworkError e){
////                new AlertDialog.Builder(this)
////                        .setTitle("Chyba připojení")
////                        .setMessage("Připojení k serveru se nezdařilo")
////                        .setPositiveButton("OK", null)
////                        .show();
//                        runOnUiThread(new Thread(){
//                            @Override
//                            public void run() {
//                                newBagBtn.setVisibility(View.VISIBLE);
//                                progress.setVisibility(View.GONE);
//                            }
//                        });
//                        Toast.makeText(BagsActivity.this, "Chyba připojení k serveru!", Toast.LENGTH_SHORT).show();
//                    } catch(DataManager.APIError e) {
//                        Toast.makeText(BagsActivity.this, "Chyba serveru!", Toast.LENGTH_SHORT).show();
//                    } catch(DataManager.ContentError e){
//                        runOnUiThread(new Thread(){
//                            @Override
//                            public void run() {
//                                newBagBtn.setVisibility(View.VISIBLE);
//                                progress.setVisibility(View.GONE);
//                            }
//                        });
//                        Toast.makeText(BagsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }.start();
//
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBags();
    }

    // TODO method necessary?
    void loadBags(){

//        adapter.setBags(null);

        new Thread(() -> {
//            bags = DataManager.get().fetchBags();
            Looper.prepare();
            try {
                bags = DataManager.bags.getBags();
            } catch (Requests.NetworkError | DataManager.APIError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(()->{
                adapter.setBags(bags);
            });
        }).start();

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
            try {
                Requests.POST(DataManager.API_URL+"/user/logout.php", new Requests.Params()).await();
            } catch(Requests.NetworkError e){
                // Log.e(getClass().getName(), "Logout failed: network error");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // abort if connection fails
                return true;
            } catch(Requests.HTTPError e){
                //Log.e(getClass().getName(), "Logout failed: "+e.message);
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
                // abort if server refuses logout
                return true;
            }
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