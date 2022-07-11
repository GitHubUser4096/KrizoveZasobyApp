package com.entscz.krizovezasoby.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.StringUtils;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;

public class AddProductActivity extends AppCompatActivity {

    static final int RESULT_SELECT_IMAGE = 1;
    static final int RESULT_TAKE_PICTURE = 2;

    int bagId;
    ImageView imagePreview;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Přidat produkt");

        ImageButton brandHint = findViewById(R.id.brandHint);
        ImageButton typeHint = findViewById(R.id.typeHint);
        ImageButton amountHint = findViewById(R.id.amountHint);
        ImageButton shortDescHint = findViewById(R.id.shortDescHint);
        ImageButton codeHint = findViewById(R.id.codeHint);
        ImageButton packageHint = findViewById(R.id.packageHint);
        ImageButton descriptionHint = findViewById(R.id.descriptionHint);

        AutoCompleteTextView brandInput = findViewById(R.id.brandInput);
        AutoCompleteTextView typeInput = findViewById(R.id.typeInput);
        EditText amountValueInput = findViewById(R.id.amountValueInput);
        Spinner amountUnitInput = findViewById(R.id.amountUnitInput);
        EditText shortDescInput = findViewById(R.id.shortDescInput);
        EditText codeInput = findViewById(R.id.codeInput);
        AutoCompleteTextView packageInput = findViewById(R.id.packageTypeInput);
        EditText descriptionInput = findViewById(R.id.descriptionInput);

        imagePreview = findViewById(R.id.imagePreview);
        Button takePicBtn = findViewById(R.id.takePicBtn);
        Button selectImageBtn = findViewById(R.id.selectImageBtn);
        Button clearImageBtn = findViewById(R.id.clearImageBtn);

        Button submitBtn = findViewById(R.id.submitBtn);

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);
        if(intent.hasExtra("code")){
            codeInput.setText(intent.getStringExtra("code"));
        }

        brandHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Výrobce nebo značka produktu - např. Heinz", Toast.LENGTH_SHORT).show();
        });
        typeHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Typ produktu - např. Fazole", Toast.LENGTH_SHORT).show();
        });
        amountHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Hmotnost nebo objem obsahu jednoho balení", Toast.LENGTH_SHORT).show();
        });
        shortDescHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Název nebo krátký popis (nápis na hlavní straně balení) - např. Heinz Beanz In a rich tomato sauce", Toast.LENGTH_SHORT).show();
        });
        codeHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Číslo, které identifikuje produkt (číslo u čárového kódu, bez mezer)", Toast.LENGTH_SHORT).show();
        });
        packageHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Jak je produkt zabalený (např. Plechovka)", Toast.LENGTH_SHORT).show();
        });
        descriptionHint.setOnClickListener(view -> {
            Toast.makeText(AddProductActivity.this, "Poznámky k produktu (zobrazují se pouze při úpravě produktu)", Toast.LENGTH_SHORT).show();
        });

        new Thread(()->{
            try {

                JSONArray brands = new JSONArray(Requests.GET(DataManager.API_URL+"/product/listBrands.php").await());
                String[] brandNames = new String[brands.length()];
                for(int i = 0; i<brands.length(); i++){
                    brandNames[i] = brands.optJSONObject(i).optString("name");
                }
                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, brandNames);

                JSONArray productTypes = new JSONArray(Requests.GET(DataManager.API_URL+"/product/listProductTypes.php").await());
                String[] productTypeNames = new String[productTypes.length()];
                for(int i = 0; i<productTypes.length(); i++){
                    productTypeNames[i] = productTypes.optJSONObject(i).optString("name");
                }
                ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productTypeNames);

                JSONArray packageTypes = new JSONArray(Requests.GET(DataManager.API_URL+"/product/listPackageTypes.php").await());
                String[] packageTypeNames = new String[packageTypes.length()];
                for(int i = 0; i<packageTypes.length(); i++){
                    packageTypeNames[i] = packageTypes.optJSONObject(i).optString("name");
                }
                ArrayAdapter<String> packageTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, packageTypeNames);

                runOnUiThread(()->{
                    brandInput.setAdapter(brandAdapter);
                    typeInput.setAdapter(productTypeAdapter);
                    packageInput.setAdapter(packageTypeAdapter);
                });

            } catch(Exception e){
                Log.e(getClass().getName(), "Failed loading suggestions", e);
            }
        }).start();

        amountUnitInput.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{
                "g", "ml"
        }));

        takePicBtn.setOnClickListener(view -> {
            Intent takePicIntent = new Intent(AddProductActivity.this, TakePictureActivity.class);
            startActivityForResult(takePicIntent, RESULT_TAKE_PICTURE);
        });

        selectImageBtn.setOnClickListener(view -> {
            Intent imgIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imgIntent.addCategory(Intent.CATEGORY_OPENABLE);
            imgIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(imgIntent, "Vybrat obrázek");
            startActivityForResult(chooserIntent, RESULT_SELECT_IMAGE);
        });

        clearImageBtn.setOnClickListener(view -> {
            imagePreview.setImageResource(android.R.drawable.ic_menu_help);
            image = null;
        });

        submitBtn.setOnClickListener(view -> {

            String imgName = null;

            if(image!=null){
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] imgBytes = os.toByteArray();
                imgName = Requests.POST_DATA(DataManager.API_URL+"/user/uploadImage.php",
                        "file", "temp.jpg", "image/jpeg", imgBytes).await();
            }

            String brand = StringUtils.toFirstUpper(brandInput.getText().toString().trim());
            String productType = StringUtils.toFirstUpper(typeInput.getText().toString().trim());
            String amountValue = amountValueInput.getText().toString().trim();
            String amountUnit = amountUnitInput.getSelectedItem().toString().trim();
            String shortDesc = StringUtils.toFirstUpper(shortDescInput.getText().toString().trim());
            String code = codeInput.getText().toString().trim();
            String packageType = packageInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            try {

                if (brand.length() == 0) {
                    brandInput.setError("Prosím vyplňte pole značka!");
                    return;
                }

                if (productType.length() == 0) {
                    typeInput.setError("Prosím vyplňte pole typ produktu!");
                    return;
                }

                if (shortDesc.length() == 0) {
                    shortDescInput.setError("Prosím vyplňte pole krátký popis!");
                    return;
                }

                if (code.length() == 0) {
                    codeInput.setError("Prosím vyplňte pole kód produktu!");
                    return;
                }

                if(amountValue.length()>0){
                    int iAmountValue;
                    try {
                        iAmountValue = Integer.parseInt(amountValue);
                    } catch(Exception e){
                        throw new DataManager.ContentError("Neplatná hodnota pole množství!");
                    }
                    if(iAmountValue<=0) throw new DataManager.ContentError("Množství musí být větší než 0!");
                    if(iAmountValue>99999) throw new DataManager.ContentError("Hodnota množství je příliš velká!");
                }

//                if(brand.length()==0) throw new RuntimeException("Prosím vyplňte pole značka!");
//                if(productType.length()==0) throw new RuntimeException("Prosím vyplňte pole typ produktu!");
//                if(shortDesc.length()==0) throw new RuntimeException("Prosím vyplňte pole krátký popis!");
//                if(code.length()==0) throw new RuntimeException("Prosím vyplňte pole kód produktu!");

                Product product = DataManager.products.createProduct(brand, productType, amountValue, amountUnit, shortDesc, code, packageType, description, imgName);

                Intent addItemIntent = new Intent(AddProductActivity.this, AddItemActivity.class);
                addItemIntent.putExtra("productId", product.id);
                addItemIntent.putExtra("bagId", bagId);
                startActivity(addItemIntent);

                finish();

            } catch(Requests.NetworkError | DataManager.APIError | DataManager.ContentError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        try {
            if (requestCode == RESULT_SELECT_IMAGE) {
                Uri uri = data.getData();
                FileDescriptor file = getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor();
                setSelectedImage(BitmapFactory.decodeFileDescriptor(file));
            } else if (requestCode == RESULT_TAKE_PICTURE) {
                String path = data.getStringExtra("path");
                setSelectedImage(getBitmap(path));
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    Bitmap getBitmap(String path){

        try {

            // load bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path));

            // rotate bitmap
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            int angle = 0;

            switch(orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
            }

//            if(angle==0) return bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // clip bitmap to be a square
            int x, y, width, height;
            if(rotated.getWidth()>rotated.getHeight()){
                width = height = rotated.getHeight();
                x = (rotated.getWidth() - width) / 2;
                y = 0;
            } else {
                width = height = rotated.getWidth();
                x = 0;
                y = (rotated.getHeight() - height) / 2;
            }

            Bitmap clipped = Bitmap.createBitmap(rotated, x, y, width, height);

            // scale bitmap to a consistent resolution
            Bitmap scaled = Bitmap.createScaledBitmap(clipped, 640, 640, true);

            return scaled;

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    void setSelectedImage(Bitmap bitmap){
        image = bitmap;
        imagePreview.setImageBitmap(image);
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