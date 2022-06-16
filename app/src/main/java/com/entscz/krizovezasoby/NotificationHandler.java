package com.entscz.krizovezasoby;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationHandler extends BroadcastReceiver {

    public NotificationHandler(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.i("zasoby", "Notification broadcast received: "+(System.currentTimeMillis()/1000));
//        Toast.makeText(context, "Notification broadcast received!", Toast.LENGTH_SHORT).show();

//        Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
//        String res = Requests.GET("https://zasoby.nggcv.cz/api/user/auth.php").await();
//        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);

        if(storedEmail==null || storedPassword==null) return;

        Requests.POST("https://zasoby.nggcv.cz/api/user/auth.php", new Requests.Params()
                .add("email", storedEmail)
                .add("password", storedPassword)
        ).await();

        try {

            JSONObject state = new JSONObject(Requests.GET("https://zasoby.nggcv.cz/api/user/checkState.php").await());
            JSONArray requiringAttention = state.optJSONArray("requiringAttention");

            if(requiringAttention.length()==0) return;

            String items = "Tyto položky vyžadují vaší pozornost:\n";

            for(int i = 0; i<requiringAttention.length(); i++){
                JSONObject item = requiringAttention.getJSONObject(i);
                items += " - "+item.getString("message")+"\n";
            }

            // TODO global setup
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("1" , "Stav položek", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Upozornění na změnu stavu položek");
                context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
            }

            Intent notificationIntent = new Intent(context, LoginActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            // TODO channel ID
            NotificationCompat.Builder bld = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Krizové zásoby")
                    .setContentText("Některé vaše zásoby vyžadují vaší pozornost!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(items))
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

            NotificationManagerCompat.from(context).notify(1, bld.build());

        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }

}
