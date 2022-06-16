package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    int notificationHours;
    int notificationMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Nastavení");

        EditText criticalValueInput = findViewById(R.id.criticalValue);
        Spinner criticalUnitSelect = findViewById(R.id.criticalUnit);
        EditText warnValueInput = findViewById(R.id.warnValue);
        Spinner warnUnitSelect = findViewById(R.id.warnUnit);
        EditText recommendedValueInput = findViewById(R.id.recommendedValue);
        Spinner recommendedUnitSelect = findViewById(R.id.recommendedUnit);
        Spinner dateFormatSelect = findViewById(R.id.dateFormat);
        Switch notificationSwitch = findViewById(R.id.notificationSwitch);
        Switch mailNotifSwitch = findViewById(R.id.mailNotifSwitch);
        Button notificationTimeBtn = findViewById(R.id.notificationTimeBtn);
        Button saveBtn = findViewById(R.id.saveBtn);

        criticalUnitSelect.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Dny", "Týdny"}));
        warnUnitSelect.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Dny", "Týdny"}));
        recommendedUnitSelect.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Dny", "Týdny"}));

        dateFormatSelect.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{
                "YYYY-MM-DD", "DD. MM. YYYY"
        }));

        try {

            JSONObject settings = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/user/getSettings.php").await());

            String[] criticalTime = settings.optString("criticalTime").split(" ");
            String criticalValue = criticalTime[0];
            String criticalUnit = criticalTime[1];
            criticalValueInput.setText(criticalValue);
            if(criticalUnit.equals("weeks")) criticalUnitSelect.setSelection(1);
            else criticalUnitSelect.setSelection(0);

            String[] warnTime = settings.optString("warnTime").split(" ");
            String warnValue = warnTime[0];
            String warnUnit = warnTime[1];
            warnValueInput.setText(warnValue);
            if(warnUnit.equals("weeks")) warnUnitSelect.setSelection(1);
            else warnUnitSelect.setSelection(0);

            String[] recommendedTime = settings.optString("recommendedTime").split(" ");
            String recommendedValue = recommendedTime[0];
            String recommendedUnit = recommendedTime[1];
            recommendedValueInput.setText(recommendedValue);
            if(recommendedUnit.equals("weeks")) recommendedUnitSelect.setSelection(1);
            else recommendedUnitSelect.setSelection(0);

            String dateFormat = settings.optString("dateFormat");
            if(dateFormat.equals("d. m. Y")) dateFormatSelect.setSelection(1);

            boolean sendNotifs = settings.optInt("sendNotifs")>0;
            mailNotifSwitch.setChecked(sendNotifs);

        } catch(Exception e){
            throw new RuntimeException(e);
        }

        SharedPreferences prefs = getSharedPreferences("notifications", Context.MODE_PRIVATE);

        if(prefs.getBoolean("showNotifications", false)){
            notificationSwitch.setChecked(true);
        } else {
            notificationSwitch.setChecked(false);
            notificationTimeBtn.setVisibility(View.GONE);
        }

        notificationHours = prefs.getInt("hours", 12);
        notificationMinutes = prefs.getInt("minutes", 0);

        notificationTimeBtn.setText("Čas oznámení: "+String.format("%02d", notificationHours)+":"+String.format("%02d", notificationMinutes));

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                notificationTimeBtn.setVisibility(View.VISIBLE);
            } else {
                notificationTimeBtn.setVisibility(View.GONE);
            }
        });

        notificationTimeBtn.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                notificationHours = hourOfDay;
                notificationMinutes = minute;
                notificationTimeBtn.setText("Čas oznámení: "+String.format("%02d", hourOfDay)+":"+String.format("%02d", minute));
            }, notificationHours, notificationMinutes, true).show();
        });

        saveBtn.setOnClickListener(v -> {

            String[] stateUnits = {"days", "weeks"};

            String criticalValue = criticalValueInput.getText().toString();
            String criticalUnit = stateUnits[criticalUnitSelect.getSelectedItemPosition()];
            String criticalTime = criticalValue+" "+criticalUnit;

            String warnValue = warnValueInput.getText().toString();
            String warnUnit = stateUnits[warnUnitSelect.getSelectedItemPosition()];
            String warnTime = warnValue+" "+warnUnit;

            String recommendedValue = recommendedValueInput.getText().toString();
            String recommendedUnit = stateUnits[recommendedUnitSelect.getSelectedItemPosition()];
            String recommendedTime = recommendedValue+" "+recommendedUnit;

            String[] dateFormats = {"Y-m-d", "d. m. Y"};
            String dateFormat = dateFormats[dateFormatSelect.getSelectedItemPosition()];

            int sendNotifs = mailNotifSwitch.isChecked() ? 1 : 0;

            try {

                if(criticalValue.length()==0) throw new RuntimeException("Prosím vyplňte hodnotu kritický stav!");
                if(warnValue.length()==0) throw new RuntimeException("Prosím vyplňte hodnotu varování!");
                if(recommendedValue.length()==0) throw new RuntimeException("Prosím vyplňte hodnotu doporučené odevzdání!");

                Requests.POST("https://zasoby.nggcv.cz/api/user/changeSettings.php", new Requests.Params()
                        .add("criticalTime", criticalTime)
                        .add("warnTime", warnTime)
                        .add("recommendedTime", recommendedTime)
                        .add("dateFormat", dateFormat)
                        .add("sendNotifs", sendNotifs)
                ).await();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("showNotifications", notificationSwitch.isChecked());
                editor.putInt("hours", notificationHours);
                editor.putInt("minutes", notificationMinutes);
                editor.apply();

                finish();

            } catch(Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, notificationHours);
            calendar.set(Calendar.MINUTE, notificationMinutes);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent notificationHandlerIntent = new Intent(this, NotificationHandler.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, notificationHandlerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, alarmIntent);
//            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, alarmIntent);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5*1000, alarmIntent);

            Toast.makeText(this, "Notification alarm set!", Toast.LENGTH_SHORT).show();

//            Log.i("zasoby", "Alarm set: "+(System.currentTimeMillis()/1000));

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel("1" , "Stav položek", NotificationManager.IMPORTANCE_HIGH);
//                channel.setDescription("Upozornění na změnu stavu položek");
//                getSystemService(NotificationManager.class).createNotificationChannel(channel);
//            }
//
//            // TODO channel ID
//            NotificationCompat.Builder bld = new NotificationCompat.Builder(this, "1")
//                    .setSmallIcon(R.drawable.icon)
//                    .setContentTitle("Krizové zásoby")
//                    .setContentText("Některé vaše zásoby vyžadují vaší pozornost!")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setAutoCancel(true)
//                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
//
//            NotificationManagerCompat.from(this).notify(1, bld.build());

        });

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