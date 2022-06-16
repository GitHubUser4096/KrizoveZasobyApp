package com.entscz.krizovezasoby;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ItemAdapter extends BaseAdapter {

    private ItemsActivity context;
    private int bagId;
    private JSONArray items;
    private HashMap<Integer, View> views = new HashMap<>();
    private int expanded = -1;

    public ItemAdapter(ItemsActivity context, int bagId){
        this.context = context;
        this.bagId = bagId;
    }

    public void setItems(JSONArray items) {
        this.items = items;
        notifyDataSetChanged();
        expanded = -1;
//        Log.i("zasoby", "Updating items");
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

    void expandItem(int i){

        expanded = i;

        for(int j = 0; j<getCount(); j++){

//            View row = views.get(j);
            View row = getView(j);
            ConstraintLayout baseLayout = row.findViewById(R.id.baseLayout);
            ConstraintLayout expandedLayout = row.findViewById(R.id.expandedLayout);
            ImageView productImage = row.findViewById(R.id.productImage);

            if(i==j){
//                baseLayout.setVisibility(View.VISIBLE);
//                expandedLayout.setVisibility(View.VISIBLE);
//                ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
//                anim.setDuration(1000);
//                anim.addUpdateListener(valueAnimator -> {
//                    float value = (float)valueAnimator.getAnimatedValue();
////                    errorMsg.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
//                    baseLayout.setAlpha(1-value);
//                    expandedLayout.setAlpha(value);
//                });
//                anim.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        baseLayout.setVisibility(View.GONE);
//                    }
//                });
//                anim.start();
                baseLayout.setVisibility(View.GONE);
                expandedLayout.setVisibility(View.VISIBLE);

//                JSONObject item = items.optJSONObject(i);
//                JSONObject product = item.optJSONObject("product");

//                if(!product.isNull("imgName")) {
//                    new Thread(){
//                        @Override
//                        public void run() {
//                            Bitmap image = Requests.GET_BITMAP("https://zasoby.nggcv.cz/images/" + product.optString("imgName")).await();
//                            context.runOnUiThread(new Thread(){
//                                @Override
//                                public void run() {
//                                    productImage.setImageBitmap(image);
//                                }
//                            });
//                        }
//                    }.start();
//                } else {
//                    productImage.setImageResource(android.R.drawable.ic_menu_help);
//                }

//                context.runOnUiThread(new Thread(){
//                    @Override
//                    public void run() {
//                    }
//                });

            } else {
                baseLayout.setVisibility(View.VISIBLE);
                expandedLayout.setVisibility(View.GONE);
//                productImage.setImageBitmap(null);
            }
        }

    }

    public View getView(int i){
        if(views.get(i)!=null){
            return views.get(i);
        }
        return getView(i, null, null);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = context.getLayoutInflater();

        if(!hasItems()){

            View row = inflater.inflate(R.layout.empty_items, null, true);
            return row;

        }

//        if(views.get(i)!=null){
//            return views.get(i);
//        }

        View row = inflater.inflate(R.layout.item, null, true);

        views.put(i, row);

        ImageView productImage = row.findViewById(R.id.productImage);
        TextView count = row.findViewById(R.id.count);
        TextView title = row.findViewById(R.id.title);
        TextView shortDesc = row.findViewById(R.id.shortDesc);
        TextView expiration = row.findViewById(R.id.expiration);
        TextView expandedCount = row.findViewById(R.id.expandedCount);
        TextView expandedTitle = row.findViewById(R.id.expandedTitle);
        TextView expandedShortDesc = row.findViewById(R.id.expandedShortDesc);
        TextView expandedExpiration = row.findViewById(R.id.expandedExpiration);
        ImageButton editBtn = row.findViewById(R.id.editBtn);
        ImageButton useBtn = row.findViewById(R.id.useBtn);
        ImageButton moveBtn = row.findViewById(R.id.moveBtn);
        ImageButton deleteBtn = row.findViewById(R.id.deleteBtn);
        ConstraintLayout baseLayout = row.findViewById(R.id.baseLayout);
        ConstraintLayout expandedLayout = row.findViewById(R.id.expandedLayout);

        if(expanded==i){
            baseLayout.setVisibility(View.GONE);
        } else {
            expandedLayout.setVisibility(View.GONE);
        }

        baseLayout.setOnClickListener(v -> {
//            if(expandedLayout.getVisibility()==View.GONE){
//                expandedLayout.setVisibility(View.VISIBLE);
//            } else {
//                expandedLayout.setVisibility(View.GONE);
//            }
//            baseLayout.setVisibility(View.GONE);
//            expandedLayout.setVisibility(View.VISIBLE);
            expandItem(i);
        });

        expandedLayout.setOnClickListener(v -> {
            expanded = -1;
            baseLayout.setVisibility(View.VISIBLE);
//            baseLayout.setAlpha(1);
            expandedLayout.setVisibility(View.GONE);
        });

        JSONObject item = items.optJSONObject(i);
        JSONObject product = item.optJSONObject("product");

        if(!product.isNull("imgName")) {
            new Thread(){
                @Override
                public void run() {
                    Bitmap image = Requests.GET_BITMAP("https://zasoby.nggcv.cz/images/" + product.optString("imgName")).await();
                    context.runOnUiThread(new Thread(){
                        @Override
                        public void run() {
                            productImage.setImageBitmap(image);
                        }
                    });
                }
            }.start();
        } else {
            productImage.setImageResource(android.R.drawable.ic_menu_help);
        }

        editBtn.setOnClickListener(v -> {
            Intent editItemIntent = new Intent(context, EditItemActivity.class);
            editItemIntent.putExtra("bagId", bagId);
            editItemIntent.putExtra("itemId", item.optInt("id"));
            context.startActivity(editItemIntent);
        });

        useBtn.setOnClickListener(v -> {
//            new UseItemDialog(context).show();
            new UseItemDialogBuilder(context, item.optInt("count"), value -> {
                try {
                    if(value<1) throw new RuntimeException("Počet musí být větší než 0!");
                    if(value>item.optInt("count")) throw new RuntimeException("Počet nesmí být větší než počet položky!");
                    if (item.optInt("used") > 0) {
                        Requests.POST("https://zasoby.nggcv.cz/api/item/setItemUnused.php?itemId=" + item.optInt("id"), new Requests.Params()
                                .add("unuseCount", value)
                        ).await();
                    } else {
                        Requests.POST("https://zasoby.nggcv.cz/api/item/setItemUsed.php?itemId=" + item.optInt("id"), new Requests.Params()
                                .add("useCount", value)
                        ).await();
                    }
//                Intent itemsIntent = new Intent(context, ItemsActivity.class);
//                itemsIntent.putExtra("bagId", bagId);
//                itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                context.startActivity(itemsIntent);
//                context.finish();
//                    context.recreate();
                    context.loadItems();
                } catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).show();
        });

        moveBtn.setOnClickListener(v -> {

            try {
                JSONArray bags = new JSONArray(Requests.GET("https://zasoby.nggcv.cz/api/bag/listBags.php").await());
                if(bags.length()<=1) {
                    Toast.makeText(context, "Nemáte žádnou tašku do které by bylo možné přesunout položku!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch(Exception e){
                throw new RuntimeException(e);
            }

            new MoveItemDialogBuilder(context, bagId, item.optInt("count"), (moveBagId, moveCount) -> {
                try {
                    if(moveCount<1) throw new RuntimeException("Počet musí být větší než 0!");
                    if(moveCount>item.optInt("count")) throw new RuntimeException("Počet nesmí být větší než počet položky!");
                    Requests.POST("https://zasoby.nggcv.cz/api/item/moveItem.php?itemId=" + item.optInt("id"), new Requests.Params()
                            .add("bagId", moveBagId)
                            .add("moveCount", moveCount)
                    ).await();
                    Intent itemsIntent = new Intent(context, ItemsActivity.class);
                    itemsIntent.putExtra("bagId", moveBagId);
                    itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itemsIntent);
                    context.finish();
                } catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).show();
        });

        deleteBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle(product.optString("shortDesc"))
                    .setMessage("Odstranit položku?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> {

                        try {

//                            Requests.POST("https://zasoby.nggcv.cz/api/item/deleteItem.php?itemId="+itemId, "").await();
                            Requests.POST("https://zasoby.nggcv.cz/api/item/deleteItem.php?itemId="+item.optInt("id"), new Requests.Params()).await();

//                            Intent itemsIntent = new Intent(context, ItemsActivity.class);
//                            itemsIntent.putExtra("bagId", bagId);
//                            itemsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(itemsIntent);
//                            finish();
//                            context.recreate();
                            context.loadItems();

                        } catch(Exception e){
                            throw new RuntimeException(e.getMessage());
                        }

                    })
                    .setNegativeButton("Zrušit", null)
                    .show();

        });

        // TODO make this work correctly with themes
        switch (item.optString("state")){
            case "used":
                row.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.itemInactiveFG, context.getTheme()));
                break;
            case "expired":
                row.setBackgroundColor(context.getResources().getColor(R.color.expired, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case "critical":
                row.setBackgroundColor(context.getResources().getColor(R.color.critical, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                break;
            case "warn":
                row.setBackgroundColor(context.getResources().getColor(R.color.warn, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            case "recommended":
                row.setBackgroundColor(context.getResources().getColor(R.color.recommended, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                break;
            default:
                row.setBackgroundColor(context.getResources().getColor(R.color.itemDefaultBG, context.getTheme()));
                count.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                title.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                shortDesc.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                expiration.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                expandedCount.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                expandedTitle.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                expandedShortDesc.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
                expandedExpiration.setTextColor(context.getResources().getColor(R.color.itemDefaultFG, context.getTheme()));
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
        expandedTitle.setText(productTitle);
        shortDesc.setText(product.optString("shortDesc"));
        expandedShortDesc.setText(product.optString("shortDesc"));
        count.setText(item.optInt("count")+"x");
        expandedCount.setText("x"+item.optInt("count"));
        expiration.setText(item.isNull("expiration") ? "" : item.optString("displayDate"));
        expandedExpiration.setText(item.isNull("expiration") ? "" : item.optString("displayDate"));
//        row.setTooltipText(productTitle);

        return row;

    }
}
