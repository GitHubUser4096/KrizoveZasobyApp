package com.entscz.krizovezasoby;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDate;

public class BagAdapter extends BaseAdapter {

    private Activity context;
    private JSONArray bags;

    public BagAdapter(Activity context){
        this.context = context;
    }

    public void setBags(JSONArray bags) {
        this.bags = bags;
        notifyDataSetChanged();
    }

    public boolean hasBags(){
        return bags!=null && bags.length()>0;
    }

    @Override
    public int getCount() {
        return hasBags() ? bags.length() : 1;
    }

    @Override
    public Object getItem(int i) {
        if(!hasBags()) return null;
        return bags.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        if(!hasBags()) return -1;
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = context.getLayoutInflater();

        if(!hasBags()){
            return inflater.inflate(R.layout.empty_bags, null, true);
        }

        View row = inflater.inflate(R.layout.bag, null, true);

//        TextView count = row.findViewById(R.id.count);
//        TextView title = row.findViewById(R.id.title);
//        TextView shortDesc = row.findViewById(R.id.shortDesc);
//        TextView expiration = row.findViewById(R.id.expiration);

        TextView bagName = row.findViewById(R.id.bagName);

        JSONObject bag = bags.optJSONObject(i);

        bagName.setText(bag.optString("name"));

        switch(bag.optString("state")){
            case "expired":
                bagName.setBackgroundColor(context.getResources().getColor(R.color.expired, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case "critical":
                bagName.setBackgroundColor(context.getResources().getColor(R.color.critical, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case "warn":
                bagName.setBackgroundColor(context.getResources().getColor(R.color.warn, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            case "recommended":
                bagName.setBackgroundColor(context.getResources().getColor(R.color.recommended, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            case "empty":
                bagName.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                break;
            default:
                bagName.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                bagName.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                break;
        }

//        JSONObject product = item.optJSONObject("product");
////        Log.i("Zásoby", item.toString());
////        LocalDate expDate = LocalDate.parse(item.optString("expiration"));
////        int days = expDate.compareTo(LocalDate.now());
////        Log.i("Zásoby", item.has("expiration")+" "+item.optString("expiration")+" "+(item.optString("expiration").length()));
//        if(item.optInt("used")>0){
//
//            count.setTextColor(context.getResources().getColor(R.color.gray));
//            title.setTextColor(context.getResources().getColor(R.color.gray));
//            shortDesc.setTextColor(context.getResources().getColor(R.color.gray));
//            expiration.setTextColor(context.getResources().getColor(R.color.gray));
//
//        } else if(!item.isNull("expiration")) {
//
//            long days = Duration.between(LocalDate.now().atStartOfDay(), LocalDate.parse(item.optString("expiration")).atStartOfDay()).toDays();
//
//            if (days < 0) {
//                row.setBackgroundColor(context.getResources().getColor(R.color.expired));
//                count.setTextColor(context.getResources().getColor(R.color.white));
//                title.setTextColor(context.getResources().getColor(R.color.white));
//                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
//                expiration.setTextColor(context.getResources().getColor(R.color.white));
//            } else if (days < 1) {
//                row.setBackgroundColor(context.getResources().getColor(R.color.critical));
//                count.setTextColor(context.getResources().getColor(R.color.white));
//                title.setTextColor(context.getResources().getColor(R.color.white));
//                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
//                expiration.setTextColor(context.getResources().getColor(R.color.white));
//            } else if (days < 1 * 7) {
//                row.setBackgroundColor(context.getResources().getColor(R.color.warn));
//                count.setTextColor(context.getResources().getColor(R.color.black));
//                title.setTextColor(context.getResources().getColor(R.color.black));
//                shortDesc.setTextColor(context.getResources().getColor(R.color.black));
//                expiration.setTextColor(context.getResources().getColor(R.color.black));
//            } else if (days < 3 * 7) {
//                row.setBackgroundColor(context.getResources().getColor(R.color.recommended));
//                count.setTextColor(context.getResources().getColor(R.color.black));
//                title.setTextColor(context.getResources().getColor(R.color.black));
//                shortDesc.setTextColor(context.getResources().getColor(R.color.black));
//                expiration.setTextColor(context.getResources().getColor(R.color.black));
//            } else {
//                count.setTextColor(context.getResources().getColor(R.color.white));
//                title.setTextColor(context.getResources().getColor(R.color.white));
//                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
//                expiration.setTextColor(context.getResources().getColor(R.color.white));
//            }
//
//        } else {
//
//            count.setTextColor(context.getResources().getColor(R.color.white));
//            title.setTextColor(context.getResources().getColor(R.color.white));
//            shortDesc.setTextColor(context.getResources().getColor(R.color.white));
//
//        }
//
//        String productTitle = product.optString("brand")+" • "+product.optString("type")+
//                (product.isNull("amountValue") ? "" : " • "+product.optInt("amountValue")+" "+product.optString("amountUnit"));
//
//        title.setText(productTitle);
//        shortDesc.setText(product.optString("shortDesc"));
//        count.setText(item.optInt("count")+"x");
//        expiration.setText(item.isNull("expiration") ? "" : item.optString("expiration"));
////        row.setTooltipText(productTitle);

        return row;

    }
}
