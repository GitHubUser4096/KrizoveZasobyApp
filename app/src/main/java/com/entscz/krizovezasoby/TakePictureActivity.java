package com.entscz.krizovezasoby;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class TakePictureActivity extends AppCompatActivity implements CameraXConfig.Provider {

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        Log.i("zasoby", "Permission status: "+ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if(!granted){
                    new AlertDialog.Builder(this)
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
        setTitle("Vyfotit");

        previewView = findViewById(R.id.previewView);
        ImageButton takePicBtn = findViewById(R.id.takePicBtn);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch(Exception e){
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        takePicBtn.setOnClickListener(v -> {

            try {
                File file = File.createTempFile("image", "jpg", getCacheDir());
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        try {
//                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra("bitmap", bitmap);
                            resultIntent.putExtra("path", file.getAbsolutePath());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            } catch(Exception e){
                throw new RuntimeException(e);
            }

        });

    }

    private void bindPreview(ProcessCameraProvider cameraProvider){

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture, preview);
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);

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
        }

        return super.onOptionsItemSelected(item);
    }

}