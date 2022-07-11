package com.entscz.krizovezasoby.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.model.Product;

import java.util.ArrayList;

public class SearchItemAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Product> products;

    public SearchItemAdapter(Activity context){
        this.context = context;
    }

    public void setProducts(ArrayList<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    public boolean hasItems(){
        return products!=null && products.size()>0;
    }

    @Override
    public int getCount() {
        if(products==null) return 0;
        if(products.size()==0) return 1;
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        if(!hasItems()){
            View row = inflater.inflate(R.layout.item_products_empty, null, true);
            return row;
        }

        View row = inflater.inflate(R.layout.item_product, null, true);

        TextView title = row.findViewById(R.id.title);
        TextView shortDesc = row.findViewById(R.id.shortDesc);

        Product product = products.get(position);

        title.setText(product.getTitle());
        shortDesc.setText(product.shortDesc);

        return row;

    }

}
