package com.entscz.krizovezasoby;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CharityPlaceAdapter extends BaseAdapter {

    private JSONArray places;
    private Activity context;

    public CharityPlaceAdapter(Activity context){
        this.context = context;
    }

    public void setPlaces(JSONArray places){
        this.places = places;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return places==null ? 0 : places.length();
    }

    @Override
    public Object getItem(int position) {
        return places.optJSONArray(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View row = inflater.inflate(R.layout.charity_place, null, true);

        TextView charityName = row.findViewById(R.id.charityName);
        TextView placeAddress = row.findViewById(R.id.placeAddress);
        TextView placeOpenHours = row.findViewById(R.id.placeOpenHours);
        TextView placeContacts = row.findViewById(R.id.placeContacts);

        JSONObject place = places.optJSONObject(position);

        String placeContact = place.isNull("contacts") ? "" : "\n"+place.optString("contacts");

        charityName.setText(place.optJSONObject("charity").optString("name"));
        placeAddress.setText(place.optString("street")+", "+place.optString("place")+", "+place.optString("postCode"));
        placeOpenHours.setText(place.optString("openHours"));
        placeContacts.setText(place.optJSONObject("charity").optString("contacts")+placeContact);

        return row;

    }
}
