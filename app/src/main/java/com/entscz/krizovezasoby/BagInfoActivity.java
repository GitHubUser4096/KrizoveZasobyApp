package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.StringUtils;

import org.json.JSONObject;

public class BagInfoActivity extends AppCompatActivity {

    int bagId;
    String bagName;
    boolean bagEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag_info);

        EditText nameInput = findViewById(R.id.nameInput);
        EditText descriptionInput = findViewById(R.id.descriptionInput);
        Button submitBtn = findViewById(R.id.submitBtn);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", 0);
        bagEmpty = intent.getBooleanExtra("isEmpty", false);

        try {

            JSONObject bagInfo = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/bag/getInfo.php?bagId="+bagId).await());

            bagName = bagInfo.optString("name");

            setTitle(bagName);
            nameInput.setText(bagName);
            if(!bagInfo.isNull("description")) descriptionInput.setText(bagInfo.optString("description"));

        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitBtn.setOnClickListener(v -> {

            String name = StringUtils.toFirstUpper(nameInput.getText().toString().trim());
            String description = descriptionInput.getText().toString();

            try {

                if(name.length()==0) throw new RuntimeException("Prosím zadejte název tašky!");

//                Requests.POST("https://zasoby.nggcv.cz/api/bag/updateInfo.php?bagId="+bagId,
//                        "name="+name+
//                                "&description="+description
//                ).await();

                Requests.POST("https://zasoby.nggcv.cz/api/bag/updateInfo.php?bagId="+bagId, new Requests.Params()
                        .add("name", name)
                        .add("description", description)
                ).await();

//                finish();
                Intent itemsIntent = new Intent(BagInfoActivity.this, ItemsActivity.class);
                itemsIntent.putExtra("bagId", bagId);
                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(itemsIntent);
                finish();

            } catch(Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bag_info, menu);
//        menu.getItem(0).setEnabled(false);
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
                    .setTitle(bagName)
                    .setMessage("Odstranit tašku?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> {

                        try {

                            Requests.POST("https://zasoby.nggcv.cz/api/bag/deleteBag.php?bagId="+bagId, "").await();

                            Intent bagsIntent = new Intent(BagInfoActivity.this, BagsActivity.class);
                            bagsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(bagsIntent);
                            finish();

                        } catch(Exception e){
                            Toast.makeText(this, "Nelze smazat tašku: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Zrušit", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

}