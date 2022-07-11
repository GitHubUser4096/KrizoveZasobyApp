package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.entscz.krizovezasoby.adapters.CharityPlaceAdapter;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

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

        CharityPlaceAdapter charityAdapter = new CharityPlaceAdapter(this);
        charityList.setAdapter(charityAdapter);

        try {

            charityAdapter.setPlaces(DataManager.get().getCharityPlaces());

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        donateBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(DonateBagActivity.this)
                    .setTitle("Odevzdat tašku?")
                    .setMessage("Taška bude označena jako odevzdaná a bude odebrána ze seznamu tašek.")
                    .setNegativeButton("Zrušit", null)
                    .setPositiveButton("Odevzdat", (dialog, which) -> {
                        try {

                            // TODO DataManager?
                            Requests.POST(DataManager.API_URL+"/bag/donateBag.php?bagId="+bagId, new Requests.Params()).await();

                            Intent bagIntent = new Intent(DonateBagActivity.this, BagsActivity.class);
                            bagIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(bagIntent);
                            finish();

                        } catch(Requests.NetworkError e){
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch(Requests.HTTPError e){
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
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