package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.BagInfo;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.StringUtils;

public class BagInfoActivity extends AppCompatActivity {

    int bagId;
    boolean bagEmpty;
    BagInfo bagInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText nameInput = findViewById(R.id.nameInput);
        EditText descriptionInput = findViewById(R.id.descriptionInput);
        Button submitBtn = findViewById(R.id.submitBtn);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", 0);
        bagEmpty = DataManager.items.getItems(bagId).size()==0;

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO nice, but does not prevent saving existing name
                if(s.length()>0){
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
                }
            }
        });

        try {

            bagInfo = DataManager.bags.getBagInfo(bagId);

            setTitle(bagInfo.name);
            nameInput.setText(bagInfo.name);
            descriptionInput.setText(bagInfo.description);

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        submitBtn.setOnClickListener(v -> {

            String name = StringUtils.toFirstUpper(nameInput.getText().toString().trim());
            String description = descriptionInput.getText().toString();

            try {

//                if(name.length()==0){
//                    nameInput.setError("Prosím zadejte název tašky!");
//                    return;
//                }

                try {
                    DataManager.bags.updateBagInfo(bagId, name, description);
                } catch(DataManager.ContentError | Requests.NetworkError e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent itemsIntent = new Intent(BagInfoActivity.this, ItemsActivity.class);
                itemsIntent.putExtra("bagId", bagId);
                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(itemsIntent);
                finish();

            } catch(Requests.NetworkError | DataManager.ContentError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bag_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

        if(item.getItemId()==R.id.deleteBag){

            if(!bagEmpty){
                Toast.makeText(this, "Taška není prázdná!", Toast.LENGTH_SHORT).show();
                return true;
            }

            new AlertDialog.Builder(this)
                    .setTitle(bagInfo.name)
                    .setMessage("Odstranit tašku?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> {

                        try {

                            DataManager.bags.deleteBag(bagId);

                            Intent bagsIntent = new Intent(BagInfoActivity.this, BagsActivity.class);
                            bagsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(bagsIntent);
                            finish();

                        } catch(Requests.NetworkError | DataManager.ContentError e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Zrušit", null)
                    .show();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}