package com.entscz.krizovezasoby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);

        if(storedEmail!=null && storedPassword!=null){

            try {

                // TODO replace by const API_URL
                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php",
                        "email="+storedEmail
                                +"&password="+storedPassword
                ).await();

                Intent bagsIntent = new Intent(LoginActivity.this, BagsActivity.class);
                startActivity(bagsIntent);
                finish();
                return;

            } catch(Exception e){
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        setTitle("Přihlásit se");

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(view -> {

            try {

                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php",
                        "email="+email
                               +"&password="+password
                ).await();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Intent bagsIntent = new Intent(LoginActivity.this, BagsActivity.class);
                startActivity(bagsIntent);
                finish();

            } catch(Exception e){
                Log.e("zasoby", e.getMessage());
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}