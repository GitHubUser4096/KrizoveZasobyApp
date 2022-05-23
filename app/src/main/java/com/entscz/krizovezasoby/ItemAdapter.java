package com.entscz.krizovezasoby;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ItemAdapter extends BaseAdapter {

    private Activity context;
    private JSONArray items;

    public ItemAdapter(Activity context){
        this.context = context;
    }

    public void setItems(JSONArray items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public boolean hasItems(){
        return items!=null && items.length()>0;
    }

    @Override
    public int getCount() {
        return hasItems() ? items.length() : 1;
    }

    @Override
    public Object getItem(int i) {
        if(!hasItems()) return null;
        return items.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        if(!hasItems()) return -1;
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = context.getLayoutInflater();

        if(!hasItems()){

            View row = inflater.inflate(R.layout.empty_items, null, true);
            return row;

        }

        View row = inflater.inflate(R.layout.item, null, true);

        TextView count = row.findViewById(R.id.count);
        TextView title = row.findViewById(R.id.title);
        TextView shortDesc = row.findViewById(R.id.shortDesc);
        TextView expiration = row.findViewById(R.id.expiration);

        JSONObject item = items.optJSONObject(i);
        JSONObject product = item.optJSONObject("product");

        switch (item.optString("state")){
            case "used":
                row.setBackgroundColor(context.getResources().getColor(R.color.black));
                count.setTextColor(context.getResources().getColor(R.color.gray));
                title.setTextColor(context.getResources().getColor(R.color.gray));
                shortDesc.setTextColor(context.getResources().getColor(R.color.gray));
                expiration.setTextColor(context.getResources().getColor(R.color.gray));
                break;
            case "expired":
                row.setBackgroundColor(context.getResources().getColor(R.color.expired));
                count.setTextColor(context.getResources().getColor(R.color.white));
                title.setTextColor(context.getResources().getColor(R.color.white));
                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
                expiration.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case "critical":
                row.setBackgroundColor(context.getResources().getColor(R.color.critical));
                count.setTextColor(context.getResources().getColor(R.color.white));
                title.setTextColor(context.getResources().getColor(R.color.white));
                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
                expiration.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case "warn":
                row.setBackgroundColor(context.getResources().getColor(R.color.warn));
                count.setTextColor(context.getResources().getColor(R.color.black));
                title.setTextColor(context.getResources().getColor(R.color.black));
                shortDesc.setTextColor(context.getResources().getColor(R.color.black));
                expiration.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case "recommended":
                row.setBackgroundColor(context.getResources().getColor(R.color.recommended));
                count.setTextColor(context.getResources().getColor(R.color.black));
                title.setTextColor(context.getResources().getColor(R.color.black));
                shortDesc.setTextColor(context.getResources().getColor(R.color.black));
                expiration.setTextColor(context.getResources().getColor(R.color.black));
                break;
            default:
                row.setBackgroundColor(context.getResources().getColor(R.color.black));
                count.setTextColor(context.getResources().getColor(R.color.white));
                title.setTextColor(context.getResources().getColor(R.color.white));
                shortDesc.setTextColor(context.getResources().getColor(R.color.white));
                expiration.setTextColor(context.getResources().getColor(R.color.white));
                break;
        }

//        Log.i("Zásoby", item.toString());
//        LocalDate expDate = LocalDate.parse(item.optString("expiration"));
//        int days = expDate.compareTo(LocalDate.now());
//        Log.i("Zásoby", item.has("expiration")+" "+item.optString("expiration")+" "+(item.optString("expiration").length()));
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

        String productTitle = product.optString("brand")+" • "+product.optString("type")+
                (product.isNull("amountValue") ? "" : " • "+product.optInt("amountValue")+" "+product.optString("amountUnit"));

        title.setText(productTitle);
        shortDesc.setText(product.optString("shortDesc"));
        count.setText(item.optInt("count")+"x");
        expiration.setText(item.isNull("expiration") ? "" : item.optString("displayDate"));
//        row.setTooltipText(productTitle);

        return row;

    }
}
