package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.number.NumberFormatter;
import android.icu.util.MeasureUnit;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Arrays;

public class AddProductActivity extends AppCompatActivity {

    static final int SELECT_IMAGE = 1;
    static final int TAKE_PICTURE = 2;

    int bagId;
    TextView errorMsg;
    ImageView imagePreview;
    String photoPath;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Přidat produkt");

        errorMsg = findViewById(R.id.errorMsg);

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

//        layout.setOnClickListener(view -> {
//            errorMsg.setHeight(0);
//            errorMsg.setText("");
//        });

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

        try {

            JSONArray brands = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/product/listBrands.php").await());
            String[] brandNames = new String[brands.length()];
            for(int i = 0; i<brands.length(); i++){
                brandNames[i] = brands.optJSONObject(i).optString("name");
            }
            ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, brandNames);
            brandInput.setAdapter(brandAdapter);

            JSONArray productTypes = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/product/listProductTypes.php").await());
            String[] productTypeNames = new String[productTypes.length()];
            for(int i = 0; i<productTypes.length(); i++){
                productTypeNames[i] = productTypes.optJSONObject(i).optString("name");
            }
            ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productTypeNames);
            typeInput.setAdapter(productTypeAdapter);

            JSONArray packageTypes = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/product/listPackageTypes.php").await());
            String[] packageTypeNames = new String[packageTypes.length()];
            for(int i = 0; i<packageTypes.length(); i++){
                packageTypeNames[i] = packageTypes.optJSONObject(i).optString("name");
            }
            ArrayAdapter<String> packageTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, packageTypeNames);
            packageInput.setAdapter(packageTypeAdapter);

        } catch(Exception e){
            Log.e("zasoby", e.getMessage());
        }

        amountUnitInput.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{
                "g", "ml"
        }));

        takePicBtn.setOnClickListener(view -> {
            Intent takePicItent = new Intent(AddProductActivity.this, TakePictureActivity.class);
            startActivityForResult(takePicItent, TAKE_PICTURE);
//            Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////            try {
////                File file = File.createTempFile("productImage", "jpg", getExternalCacheDir());
////                photoPath = file.getAbsolutePath();
//////                Uri uri = new Uri.Builder().path(photoPath).build();
////                Uri uri = FileProvider.getUriForFile(this, "com.entscz.krizovezasoby", file);
////                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
////            } catch(Exception e){
////                throw new RuntimeException(e);
////            }
//            startActivityForResult(takePicIntent, TAKE_PICTURE);
        });

        selectImageBtn.setOnClickListener(view -> {
            Intent imgIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imgIntent.addCategory(Intent.CATEGORY_OPENABLE);
            imgIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(imgIntent, "Vybrat obrázek");
            startActivityForResult(chooserIntent, SELECT_IMAGE);
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
                imgName = Requests.POST_DATA("https://zasoby.nggcv.cz/api/user/uploadImage.php",
                        "file", "temp.jpg", "image/jpeg", imgBytes).await();
            }

            String brand = brandInput.getText().toString().trim();
            String productType = typeInput.getText().toString().trim();
            String amountValue = amountValueInput.getText().toString().trim();
            String amountUnit = amountUnitInput.getSelectedItem().toString().trim();
            String shortDesc = shortDescInput.getText().toString().trim();
            String code = codeInput.getText().toString().trim();
            String packageType = packageInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            try {

                if(brand.length()==0) throw new RuntimeException("Prosím vyplňte pole značka!");
                if(productType.length()==0) throw new RuntimeException("Prosím vyplňte pole typ produktu!");
                if(shortDesc.length()==0) throw new RuntimeException("Prosím vyplňte pole krátký popis!");
                if(code.length()==0) throw new RuntimeException("Prosím vyplňte pole kód produktu!");

//                JSONObject product = new JSONObject(Requests.POST("https://zasoby.nggcv.cz/api/product/createProduct.php",
//                        "brand="+brand+
//                                "&type="+productType+
//                                "&amountValue="+amountValue+
//                                "&amountUnit="+amountUnit+
//                                "&shortDesc="+shortDesc+
//                                "&code="+code+
//                                "&packageType="+packageType+
//                                "&description="+description+
//                                (imgName!=null ? "&imgName="+imgName : "")
//                ).await());

                JSONObject product = new JSONObject(Requests.POST("https://zasoby.nggcv.cz/api/product/createProduct.php", new Requests.Params()
                        .add("brand", brand)
                        .add("type", productType)
                        .add("amountValue", amountValue)
                        .add("amountUnit", amountUnit)
                        .add("shortDesc", shortDesc)
                        .add("code", code)
                        .add("packageType", packageType)
                        .add("description", description)
                        .add("imgName", (imgName!=null ? imgName : ""))
                ).await());

                Intent addItemIntent = new Intent(AddProductActivity.this, AddItemActivity.class);
                addItemIntent.putExtra("productId", product.optInt("id"));
                addItemIntent.putExtra("productName", product.optString("shortDesc"));
                if(!product.isNull("imgName")) addItemIntent.putExtra("imgName", product.optString("imgName"));
                addItemIntent.putExtra("bagId", bagId);
                startActivity(addItemIntent);

                finish();

            } catch(Exception e){
                Log.e("zasoby", e.getMessage());
                e.printStackTrace();
                showError(e.getMessage());
            }

        });



    }

    void showError(String msg){
        ValueAnimator anim = ValueAnimator.ofFloat(0, 26);
        anim.setDuration(100);
        anim.addUpdateListener(valueAnimator -> {
            float value = (float)valueAnimator.getAnimatedValue();
            errorMsg.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
        });
        anim.start();
//            errorMsg.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        errorMsg.setText(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        try {
            if (requestCode == SELECT_IMAGE) {
                Uri uri = data.getData();
//                imagePreview.setImageURI(uri);
                FileDescriptor file = getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor();
//                image = BitmapFactory.decodeFileDescriptor(file);
//                imagePreview.setImageBitmap(image);
                setSelectedImage(BitmapFactory.decodeFileDescriptor(file));
            } else if (requestCode == TAKE_PICTURE) {
//                image = (Bitmap) data.getExtras().get("data");
//                image = data.getParcelableExtra("bitmap");
                String path = data.getStringExtra("path");
//                image = BitmapFactory.decodeStream(new FileInputStream(path));
//                image = getBitmap(path);
//                imagePreview.setImageBitmap(image);
                setSelectedImage(getBitmap(path));

//                Log.e("zasoby", "w "+image.getWidth()+" h "+image.getHeight());

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            errorMsg.setHeight(0);
            errorMsg.setText("");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}