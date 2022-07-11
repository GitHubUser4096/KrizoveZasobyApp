package com.entscz.krizovezasoby.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.CharityPlace;

import java.util.ArrayList;

public class CharityPlaceAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<CharityPlace> places;

    public CharityPlaceAdapter(Activity context){
        this.context = context;
    }

    public void setPlaces(ArrayList<CharityPlace> places){
        this.places = places;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return places==null ? 0 : places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View row = inflater.inflate(R.layout.item_charity_place, null, true);

        TextView charityName = row.findViewById(R.id.charityName);
        TextView placeAddress = row.findViewById(R.id.placeAddress);
        TextView placeOpenHours = row.findViewById(R.id.placeOpenHours);
        TextView placeContacts = row.findViewById(R.id.placeContacts);

        CharityPlace place = places.get(position);

        charityName.setText(place.charityName);
        placeAddress.setText(place.getAddress());
        placeOpenHours.setText(place.openHours);
        placeContacts.setText(place.contacts);

        return row;

    }
}
