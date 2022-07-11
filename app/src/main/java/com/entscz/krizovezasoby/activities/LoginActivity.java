package com.entscz.krizovezasoby.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.entscz.krizovezasoby.LoginManager;
import com.entscz.krizovezasoby.NotificationHandler;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationHandler.NOTIF_CHANNEL , "Stav položek", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Upozornění na změnu stavu položek");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NetworkInfo netInfo = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if(netInfo==null || !netInfo.isConnected()){
            showInternetErrorDialog();
            return;
        }

        LoginManager.init(this);
        DataManager.init(this);

        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);

        if(storedEmail!=null && storedPassword!=null){

            SharedPreferences notifPrefs = getSharedPreferences("notifications", Context.MODE_PRIVATE);

            if(notifPrefs.getBoolean("showNotifications", false)){

                // TODO make alarm manager class?
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

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setMessage("Přihlašování...")
                    .setCancelable(false)
                    .show();

            new Thread(()->{
                Looper.prepare();
                try {

                    LoginManager.login(false);

                    dialog.dismiss();
                    Intent bagsIntent = new Intent(LoginActivity.this, BagsActivity.class);
                    startActivity(bagsIntent);
                    finish();
                    return;

                } catch(Requests.HTTPError e){
                    Toast.makeText(this, "Přihlášení se nezdařilo!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch(Requests.NetworkError e){
                    showInternetErrorDialog();
                    return;
                }
            }).start();

        }

        setTitle("Přihlásit se");

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(view -> {

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setMessage("Přihlašování...")
                    .setCancelable(false)
                    .show();

            new Thread(()->{
                Looper.prepare();
                try {

                    String email = emailInput.getText().toString();
                    String password = passwordInput.getText().toString();

//                    Requests.POST(DataManager.API_URL+"/user/auth.php", new Requests.Params()
//                            .add("email", email)
//                            .add("password", password)
//                    ).await();

                    LoginManager.login(email, password);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    dialog.dismiss();
                    Intent bagsIntent = new Intent(LoginActivity.this, BagsActivity.class);
                    startActivity(bagsIntent);
                    finish();

                } catch(Requests.HTTPError e){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch(Requests.NetworkError e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).start();

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