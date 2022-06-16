package com.entscz.krizovezasoby;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.util.Scanner;

// TODO make this an abstract scanner class?
public class ScannerActivity extends AppCompatActivity implements CameraXConfig.Provider {

    int bagId;

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    BarcodeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if(!granted){
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Nelze použít fotoaparát")
                            .setMessage("Přístup k fotoaparátu není povolen!")
                            .setOnDismissListener(dialog -> {
                                finish();
                            })
                            .setNegativeButton("OK", null)
                            .create().show();
                }
            });
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Skenovat kód");

        Intent intent = getIntent();
        bagId = intent.getIntExtra("bagId", -1);

        previewView = findViewById(R.id.previewView);

        scanner = BarcodeScanning.getClient(new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch(Exception e){
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void bindPreview(ProcessCameraProvider cameraProvider){

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            scanCode(image);
        });

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);

    }

    void scanCode(ImageProxy imageProxy){

        Image mediaImage = imageProxy.getImage();

        if(mediaImage==null) return;

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if(barcodes.size()>0){
                        onCodeScanned(barcodes.get(0).getRawValue());
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
//                    Toast.makeText(ScannerActivity.this, "Failed scanning code: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

    }

    private void onCodeScanned(String code){
//        Toast.makeText(ScannerActivity.this, "Code scanned: "+code, Toast.LENGTH_SHORT).show();

        try {

            String productJson = Requests.GET("https://zasoby.nggcv.cz/api/product/getProductByCode.php?code="+code).await();

            JSONObject product = new JSONObject(productJson);

            Intent intent = new Intent(ScannerActivity.this, AddItemActivity.class);
            intent.putExtra("bagId", bagId);
            intent.putExtra("productName", product.getString("shortDesc"));
            intent.putExtra("productId", product.getInt("id"));
            if(!product.isNull("imgName")) intent.putExtra("imgName", product.getString("imgName"));
            startActivity(intent);
            finish();

        } catch(Exception e){

            new AlertDialog.Builder(ScannerActivity.this)
                    .setTitle("Produkt nenalezen!")
                    .setItems(new String[]{
                            "Zkusit znovu", "Přidat produkt"
                    }, (dialogInterface, i) -> {
                        switch (i){
                            case 0:
                                recreate();
                                break;
                            case 1:
                                Intent addProductIntent = new Intent(ScannerActivity.this, AddProductActivity.class);
                                addProductIntent.putExtra("code", code);
                                addProductIntent.putExtra("bagId", bagId);
                                startActivity(addProductIntent);
                                finish();
                                break;
                        }
                    })
                    .setOnCancelListener(dialogInterface -> {
                        finish();
                    })
                    .create().show();

        }

        scanner.close();

    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_scanner, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

//        if(item.getItemId()==R.id.searchProducts){
//            Intent searchProductsIntent = new Intent(this, SearchItemActivity.class);
//            searchProductsIntent.putExtra("bagId", bagId);
//            startActivity(searchProductsIntent);
//            return true;
//        }
//
//        if(item.getItemId()==R.id.addProduct){
//            Intent addProductIntent = new Intent(this, AddProductActivity.class);
//            addProductIntent.putExtra("bagId", bagId);
//            startActivity(addProductIntent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}