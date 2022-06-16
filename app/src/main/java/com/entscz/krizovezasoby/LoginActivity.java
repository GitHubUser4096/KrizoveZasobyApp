package com.entscz.krizovezasoby;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetworkInfo netInfo = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if(netInfo==null || !netInfo.isConnected()){
            showInternetErrorDialog();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);

        if(storedEmail!=null && storedPassword!=null){

            SharedPreferences notifPrefs = getSharedPreferences("notifications", Context.MODE_PRIVATE);

            if(notifPrefs.getBoolean("showNotifications", false)){

                Intent notificationHandlerIntent = new Intent(this, NotificationHandler.class);
                PendingIntent existingAlarmIntent = PendingIntent.getBroadcast(this, 0, notificationHandlerIntent, PendingIntent.FLAG_NO_CREATE);

                if(existingAlarmIntent==null) {

                    int notificationHours = notifPrefs.getInt("hours", 12);
                    int notificationMinutes = notifPrefs.getInt("minutes", 0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, notificationHours);
                    calendar.set(Calendar.MINUTE, notificationMinutes);

                    AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, notificationHandlerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

                    Toast.makeText(this, "Notification alarm set!", Toast.LENGTH_SHORT).show();

                }

            }

            try {

                // TODO replace by const API_URL
//                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php",
//                        "email="+storedEmail
//                                +"&password="+storedPassword
//                ).await();

                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php", new Requests.Params()
                        .add("email", storedEmail)
                        .add("password", storedPassword)
                ).await();

                Intent bagsIntent = new Intent(LoginActivity.this, BagsActivity.class);
                startActivity(bagsIntent);
                finish();
                return;

            } catch(Exception e){
//                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                showInternetErrorDialog();
                return;
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

//                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php",
//                        "email="+email
//                               +"&password="+password
//                ).await();

                Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php", new Requests.Params()
                        .add("email", email)
                        .add("password", password)
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

    private void showInternetErrorDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Chyba připojení")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Aplikace potřebuje připojení k internetu")
                .setPositiveButton("Zkusit znovu", (dialog, which) -> {
                    recreate();
                })
                .setNegativeButton("Zrušit", (dialog, which) -> {
                    finish();
                })
                .show();
    }

}