package com.entscz.krizovezasoby.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.widget.Toast;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class ScannerActivity extends AppCompatActivity implements CameraXConfig.Provider {

    int bagId;

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    BarcodeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // TODO permission manager?
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

        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();

        if(mediaImage==null) return;

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if(barcodes.size()>0){
                        // TODO consider all scanned codes
                        onCodeScanned(barcodes.get(0).getRawValue());
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(getClass().getName(), "Failed scanning code");
                    e.printStackTrace();
                });

    }

    private void onCodeScanned(String code){

        try {

            Product product = DataManager.products.getProductByCode(code);

            Intent intent = new Intent(ScannerActivity.this, AddItemActivity.class);
            intent.putExtra("bagId", bagId);
            intent.putExtra("productId", product.id);
            startActivity(intent);
            finish();

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        } catch(DataManager.ContentError e){

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
//                        finish();
                        recreate();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}