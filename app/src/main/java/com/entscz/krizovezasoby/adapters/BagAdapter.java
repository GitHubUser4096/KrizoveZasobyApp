package com.entscz.krizovezasoby.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.Bag;

import java.util.ArrayList;

public class BagAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Bag> bags;

    public BagAdapter(Activity context){
        this.context = context;
    }

    public void setBags(ArrayList<Bag> bags){
        this.bags = bags;
        notifyDataSetChanged();
    }

    public boolean hasBags(){
        return bags!=null && bags.size()>0;
    }

    @Override
    public int getCount() {
        return hasBags() ? bags.size() : 1;
    }

    @Override
    public Object getItem(int i) {
        if(!hasBags()) return null;
        return bags.get(i);
    }

    @Override
    public long getItemId(int i) {
        if(!hasBags()) return -1;
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = context.getLayoutInflater();

        if(bags==null){
            return inflater.inflate(R.layout.item_bags_loading, null, true);
        }

        if(bags.size()==0){
            return inflater.inflate(R.layout.item_bags_empty, null, true);
        }

        View row = inflater.inflate(R.layout.item_bag, null, true);

        TextView bagName = row.findViewById(R.id.bagName);

        Bag bag = bags.get(i);

        bagName.setText(bag.name);

        switch(bag.state){
            case EXPIRED:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.expired, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case CRITICAL:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.critical, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case WARN:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.warn, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            case RECOMMENDED:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.recommended, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            case EMPTY:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                break;
            default:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                break;
        }

        return row;

    }
}
