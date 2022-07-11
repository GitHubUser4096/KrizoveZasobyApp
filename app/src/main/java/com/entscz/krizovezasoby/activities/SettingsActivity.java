package com.entscz.krizovezasoby.activities;

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

import com.entscz.krizovezasoby.NotificationHandler;
import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Settings;
import com.entscz.krizovezasoby.util.Requests;
import com.entscz.krizovezasoby.util.ValueError;

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

            Settings settings = DataManager.get().fetchSettings();

            criticalValueInput.setText(settings.criticalValue);
            if(settings.criticalUnit.equals("weeks")) criticalUnitSelect.setSelection(1);
            else criticalUnitSelect.setSelection(0);

            warnValueInput.setText(settings.warnValue);
            if(settings.warnUnit.equals("weeks")) warnUnitSelect.setSelection(1);
            else warnUnitSelect.setSelection(0);

            recommendedValueInput.setText(settings.recommendedValue);
            if(settings.recommendedUnit.equals("weeks")) recommendedUnitSelect.setSelection(1);
            else recommendedUnitSelect.setSelection(0);

            if(settings.dateFormat.equals("d. m. Y")) dateFormatSelect.setSelection(1);

            mailNotifSwitch.setChecked(settings.sendNotifs);

        } catch(Requests.NetworkError | DataManager.APIError e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
            return;
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

                if(criticalValue.length()==0) throw new ValueError("Prosím vyplňte hodnotu kritický stav!");
                if(warnValue.length()==0) throw new ValueError("Prosím vyplňte hodnotu varování!");
                if(recommendedValue.length()==0) throw new ValueError("Prosím vyplňte hodnotu doporučené odevzdání!");

                int iCritVal = Integer.parseInt(criticalValue);
                int iWarnVal = Integer.parseInt(warnValue);
                int iRecVal = Integer.parseInt(recommendedValue);

                if(iCritVal<=0) throw new ValueError("Hodnota pole kritický stav musí být větší než 0!");
                if(iWarnVal<=0) throw new ValueError("Hodnota pole varování musí být větší než 0!");
                if(iRecVal<=0) throw new ValueError("Hodnota pole doporučené odevzdání musí být větší než 0!");

                Requests.POST(DataManager.API_URL+"/user/changeSettings.php", new Requests.Params()
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

            } catch(Requests.NetworkError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } catch(Requests.HTTPError e){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
                return;
            } catch(ValueError e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO notification manager
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, notificationHours);
            calendar.set(Calendar.MINUTE, notificationMinutes);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent notificationHandlerIntent = new Intent(this, NotificationHandler.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, notificationHandlerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            if(notificationSwitch.isChecked()){
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                Toast.makeText(this, "Notification alarm set!", Toast.LENGTH_SHORT).show();
            } else {
                alarmMgr.cancel(alarmIntent);
                Toast.makeText(this, "Notification alarm cancelled!", Toast.LENGTH_SHORT).show();
            }

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