package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.osmdroid.views.MapView;

public class TempDonateActivity extends AppCompatActivity {

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Najít charitu");

//        Context ctx = getApplicationContext();
//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_donate_temp);

        TabLayout tabMenu = findViewById(R.id.tabMenu);
        ViewPager2 tabContent = findViewById(R.id.tabContent);
        tabContent.setAdapter(new DonatePagerAdapter(this));
        new TabLayoutMediator(tabMenu, tabContent, (tab, position) -> {
            switch (position){
                default:
                case 0:
                    tab.setText("Seznam");
                    return;
                case 1:
                    tab.setText("Mapa");
                    return;
            }
        }).attach();

//        TabLayout tabMenu = findViewById(R.id.tabMenu);
//        ConstraintLayout tabContent = findViewById(R.id.tabContent);
//
//        tabMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
////                tabContent.view
//                if(tab.getText().equals("Seznam")){
//                    getLayoutInflater().inflate(R.layout.donate_list, tabContent, true);
//                } else if(tab.getText().equals("Mapa")){
//                    getLayoutInflater().inflate(R.layout.donate_map, tabContent, true);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

//        map = findViewById(R.id.map);
//        map.setTileSource(TileSourceFactory.MAPNIK);
//        map.setMultiTouchControls(true);
//        map.setMaxZoomLevel(19.0);
//
//        IMapController mapController = map.getController();
//        mapController.setZoom(14.0);
//        mapController.setCenter(new GeoPoint(50.075539, 14.437800));
//
////        Marker marker = new Marker(map);
////        marker.setPosition(new GeoPoint(50.075539, 14.437800));
////        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
////        map.getOverlays().add(marker);
//
//        try {
//
//            JSONArray places = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/charity/listPlaces.php").await());
//
//            for(int i = 0; i<places.length(); i++){
//                JSONObject place = places.optJSONObject(i);
//                double lat = Double.parseDouble(place.optString("latitude"));
//                double lon = Double.parseDouble(place.optString("longitude"));
//                String title = place.optJSONObject("charity").optString("name")+" - "+place.optString("street")+", "+place.optString("place");
//                String desc = "Otevírací doba: "+place.optString("openHours")+
//                        "\nKontakty:\n"+place.optJSONObject("charity").optString("contacts")+"\n"+place.optString("contacts");
//                Marker marker = new Marker(map);
//                marker.setPosition(new GeoPoint(lat, lon));
//                marker.setTitle(title);
//                marker.setSubDescription(desc);
////                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//                map.getOverlays().add(marker);
//            }
//
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        map.onPause();
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