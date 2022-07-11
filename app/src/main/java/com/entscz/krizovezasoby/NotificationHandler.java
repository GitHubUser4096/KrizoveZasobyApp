package com.entscz.krizovezasoby;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationHandler extends BroadcastReceiver {

    public static String NOTIF_CHANNEL = "com.entscz.krizovezasoby.itemState";
    public static int NOTIF_ID = 1;

    public NotificationHandler(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);

        if(storedEmail==null || storedPassword==null) return;

        NetworkInfo netInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if(netInfo==null || !netInfo.isConnected()) return;

        try {

            // TODO auth manager
//            Requests.POST(DataManager.API_URL+"/user/auth.php", new Requests.Params()
//                    .add("email", storedEmail)
//                    .add("password", storedPassword)
//            ).await();
            LoginManager.init(context);
            LoginManager.login(false);

            // TODO use DataManager
            JSONObject state = new JSONObject(Requests.GET(DataManager.API_URL+"/user/checkState.php").await());
            JSONArray requiringAttention = state.optJSONArray("requiringAttention");

            if(requiringAttention.length()==0) return;

            String items = "Tyto položky vyžadují vaší pozornost:\n";

            for(int i = 0; i<requiringAttention.length(); i++){
                JSONObject item = requiringAttention.getJSONObject(i);
                items += " - "+item.getString("message")+"\n";
            }

            Intent notificationIntent = new Intent(context, LoginActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder bld = new NotificationCompat.Builder(context, NOTIF_CHANNEL)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Krizové zásoby")
                    .setContentText("Některé vaše zásoby vyžadují vaší pozornost!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(items))
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

            NotificationManagerCompat.from(context).notify(NOTIF_ID, bld.build());

        } catch(Exception e){
            Log.e(getClass().getName(), "Failed ", e);
        }

    }

}
