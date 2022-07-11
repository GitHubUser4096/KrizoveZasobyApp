package com.entscz.krizovezasoby;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;

public class LoginManager {

    private static Context context;
    private static CookieManager cookieManager;

    public static void init(Context context){

//        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        LoginManager.context = context;

        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

    }

    public static void login(boolean reauth){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        String storedEmail = prefs.getString("email", null);
        String storedPassword = prefs.getString("password", null);
        String storedSessId = prefs.getString("sessId", null);

        if(!reauth && storedSessId!=null){
            try {
                HttpCookie cookie = new HttpCookie("PHPSESSID", storedSessId);
                cookie.setVersion(0);
                cookie.setPath("/");
                cookie.setDomain("zasoby.nggcv.cz");
                cookieManager.getCookieStore().add(new URI(DataManager.BASE_URL), cookie);
                Log.i(LoginManager.class.getName(), "Session cookie set");
            } catch(Exception e){
                Log.e(LoginManager.class.getName(), "Failed setting session cookie", e);
            }
        }

        login(storedEmail, storedPassword);

    }

    public static void login(String email, String password){

        Requests.POST(DataManager.API_URL+"/user/auth.php", new Requests.Params()
                .add("email", email)
                .add("password", password)
        ).await();

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String storedSessId = prefs.getString("sessId", null);

        for(HttpCookie cookie: cookieManager.getCookieStore().getCookies()){
            if(cookie.getName().equals("PHPSESSID")){
                String sessId = cookie.getValue();
                prefs.edit().putString("sessId", sessId).apply();
                Log.i(LoginManager.class.getName(), "Saved session cookie");
                Log.i(LoginManager.class.getName(), "Stored id: "+storedSessId+" received id: "+cookie.getValue());
                break;
            }
        }

    }

}
