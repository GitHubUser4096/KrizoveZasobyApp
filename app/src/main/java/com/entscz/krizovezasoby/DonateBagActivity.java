package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

public class DonateBagActivity extends AppCompatActivity {

    int bagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_bag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Najít charitu");

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        ListView charityList = findViewById(R.id.charityList);
        Button donateBtn = findViewById(R.id.donateBtn);

//        ArrayAdapter<String> charityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        CharityPlaceAdapter charityAdapter = new CharityPlaceAdapter(this);
        charityList.setAdapter(charityAdapter);

        try {

            JSONArray places = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/charity/listPlaces.php").await());

            charityAdapter.setPlaces(places);

//            String[] placeNames = new String[places.length()];

//            for(int i = 0; i<places.length(); i++){
//                JSONObject place = places.optJSONObject(i);
////                double lat = Double.parseDouble(place.optString("latitude"));
////                double lon = Double.parseDouble(place.optString("longitude"));
//                String title = place.optJSONObject("charity").optString("name")+" - "+place.optString("street")+", "+place.optString("place");
////                String desc = "Otevírací doba: "+place.optString("openHours")+
////                        "\nKontakty:\n"+place.optJSONObject("charity").optString("contacts")+"\n"+place.optString("contacts");
//                placeNames[i] = title;
//            }
//
//            charityAdapter.addAll(placeNames);

        } catch(Exception e){
            throw new RuntimeException(e);
        }

        donateBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(DonateBagActivity.this)
                    .setTitle("Odevzdat tašku?")
                    .setMessage("Taška bude označena jako odevzdaná a bude odebrána ze seznamu tašek.")
                    .setNegativeButton("Zrušit", null)
                    .setPositiveButton("Odevzdat", (dialog, which) -> {
                        try {

                            Requests.POST("https://zasoby.nggcv.cz/api/bag/donateBag.php?bagId="+bagId, new Requests.Params()).await();

                            Intent bagIntent = new Intent(DonateBagActivity.this, BagsActivity.class);
                            bagIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(bagIntent);
                            finish();

                        } catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }).show();

        });

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