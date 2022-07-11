package com.entscz.krizovezasoby.adapters;

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

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.activities.EditItemActivity;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.dialogs.MoveItemDialog;
import com.entscz.krizovezasoby.dialogs.UseItemDialog;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.model.ItemManager;
import com.entscz.krizovezasoby.util.Requests;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemAdapter extends BaseAdapter {

    private ItemsActivity context;
    private int bagId;
    private ArrayList<Item> items;
    private HashMap<Integer, View> views = new HashMap<>();
    private int expanded = -1;

    public ItemAdapter(ItemsActivity context, int bagId){
        this.context = context;
        this.bagId = bagId;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
        expanded = -1; // TODO why is this after notifyDataSetChanged?
    }

    public boolean hasItems(){
        return items!=null && items.size()>0;
    }

    @Override
    public int getCount() {
        return hasItems() ? items.size() : 1;
    }

    @Override
    public Object getItem(int i) {
        if(!hasItems()) return null;
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        if(!hasItems()) return -1;
        return i;
    }

    void expandItem(int i){

        expanded = i;

        for(int j = 0; j<getCount(); j++){

            View row = getView(j);
            ConstraintLayout baseLayout = row.findViewById(R.id.baseLayout);
            ConstraintLayout expandedLayout = row.findViewById(R.id.expandedLayout);
            // TODO load image here instead of on load?
//            ImageView productImage = row.findViewById(R.id.productImage);

            if(i==j){
                // TODO animations?
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

        if(items==null){
            View row = inflater.inflate(R.layout.item_items_loading, null, true);
            return row;
        }

        if(items.size()==0){
            View row = inflater.inflate(R.layout.item_items_empty, null, true);
            return row;
        }

        View row = inflater.inflate(R.layout.item_item, null, true);

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
            expandItem(i);
        });

        expandedLayout.setOnClickListener(v -> {
            expanded = -1;
            baseLayout.setVisibility(View.VISIBLE);
            expandedLayout.setVisibility(View.GONE);
        });

        Item item = items.get(i);

        productImage.setBackgroundColor(context.getColor(R.color.white));
        if(item.product.imgName!=null) {
            new Thread(()->{
                try {
                    Bitmap image = Requests.GET_BITMAP(DataManager.IMG_URL + "/" + item.product.imgName).await();
                    context.runOnUiThread(() -> {
                        productImage.setImageBitmap(image);
                    });
                } catch(Requests.HTTPError | Requests.NetworkError e){
                    Log.e(getClass().getName(), "Failed loading product image", e);
                }
            }).start();
        } else {
            productImage.setImageResource(android.R.drawable.ic_menu_help);
        }

        editBtn.setOnClickListener(v -> {
            Intent editItemIntent = new Intent(context, EditItemActivity.class);
            editItemIntent.putExtra("bagId", bagId);
            editItemIntent.putExtra("itemId", item.id);
            context.startActivity(editItemIntent);
        });

        useBtn.setOnClickListener(v -> {
            UseItemDialog.show(context, item);
        });

        moveBtn.setOnClickListener(v -> {

            try {
                if(DataManager.bags.getBags().size()<=1){
                    Toast.makeText(context, "Nemáte žádnou tašku do které by bylo možné přesunout položku!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch(Requests.NetworkError | DataManager.APIError e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            MoveItemDialog.show(context, bagId, item);

        });

        deleteBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle(item.product.shortDesc)
                    .setMessage("Odstranit položku?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> {

                        try {

                            // TODO use DataManager?
                            Requests.POST(DataManager.API_URL+"/item/deleteItem.php?itemId="+item.id, new Requests.Params()).await();

                            context.loadItems();

                        } catch(Requests.NetworkError e){
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch(Requests.HTTPError e){
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Zrušit", null)
                    .show();

        });

        switch (item.state){
            case "used":
                row.setBackgroundColor(context.getColor(R.color.itemDefaultBG));
                count.setTextColor(context.getColor(R.color.itemInactiveFG));
                title.setTextColor(context.getColor(R.color.itemInactiveFG));
                shortDesc.setTextColor(context.getColor(R.color.itemInactiveFG));
                expiration.setTextColor(context.getColor(R.color.itemInactiveFG));
                expandedCount.setTextColor(context.getColor(R.color.itemInactiveFG));
                expandedTitle.setTextColor(context.getColor(R.color.itemInactiveFG));
                expandedShortDesc.setTextColor(context.getColor(R.color.itemInactiveFG));
                expandedExpiration.setTextColor(context.getColor(R.color.itemInactiveFG));
                break;
            case "expired":
                row.setBackgroundColor(context.getColor(R.color.expired));
                count.setTextColor(context.getColor(R.color.white));
                title.setTextColor(context.getColor(R.color.white));
                shortDesc.setTextColor(context.getColor(R.color.white));
                expiration.setTextColor(context.getColor(R.color.white));
                expandedCount.setTextColor(context.getColor(R.color.white));
                expandedTitle.setTextColor(context.getColor(R.color.white));
                expandedShortDesc.setTextColor(context.getColor(R.color.white));
                expandedExpiration.setTextColor(context.getColor(R.color.white));
                break;
            case "critical":
                row.setBackgroundColor(context.getColor(R.color.critical));
                count.setTextColor(context.getColor(R.color.white));
                title.setTextColor(context.getColor(R.color.white));
                shortDesc.setTextColor(context.getColor(R.color.white));
                expiration.setTextColor(context.getColor(R.color.white));
                expandedCount.setTextColor(context.getColor(R.color.white));
                expandedTitle.setTextColor(context.getColor(R.color.white));
                expandedShortDesc.setTextColor(context.getColor(R.color.white));
                expandedExpiration.setTextColor(context.getColor(R.color.white));
                break;
            case "warn":
                row.setBackgroundColor(context.getColor(R.color.warn));
                count.setTextColor(context.getColor(R.color.black));
                title.setTextColor(context.getColor(R.color.black));
                shortDesc.setTextColor(context.getColor(R.color.black));
                expiration.setTextColor(context.getColor(R.color.black));
                expandedCount.setTextColor(context.getColor(R.color.black));
                expandedTitle.setTextColor(context.getColor(R.color.black));
                expandedShortDesc.setTextColor(context.getColor(R.color.black));
                expandedExpiration.setTextColor(context.getColor(R.color.black));
                break;
            case "recommended":
                row.setBackgroundColor(context.getColor(R.color.recommended));
                count.setTextColor(context.getColor(R.color.black));
                title.setTextColor(context.getColor(R.color.black));
                shortDesc.setTextColor(context.getColor(R.color.black));
                expiration.setTextColor(context.getColor(R.color.black));
                expandedCount.setTextColor(context.getColor(R.color.black));
                expandedTitle.setTextColor(context.getColor(R.color.black));
                expandedShortDesc.setTextColor(context.getColor(R.color.black));
                expandedExpiration.setTextColor(context.getColor(R.color.black));
                break;
            default:
                row.setBackgroundColor(context.getColor(R.color.itemDefaultBG));
                count.setTextColor(context.getColor(R.color.itemDefaultFG));
                title.setTextColor(context.getColor(R.color.itemDefaultFG));
                shortDesc.setTextColor(context.getColor(R.color.itemDefaultFG));
                expiration.setTextColor(context.getColor(R.color.itemDefaultFG));
                expandedCount.setTextColor(context.getColor(R.color.itemDefaultFG));
                expandedTitle.setTextColor(context.getColor(R.color.itemDefaultFG));
                expandedShortDesc.setTextColor(context.getColor(R.color.itemDefaultFG));
                expandedExpiration.setTextColor(context.getColor(R.color.itemDefaultFG));
                break;
        }

//        String productTitle = item.product.getTitle();

        String productTitle;

        if(DataManager.items.getDisplay()==ItemManager.DISPLAY_TYPE_FIRST){
            productTitle = item.product.type+" • "+item.product.brand+
                    (item.product.amountValue==-1 ? "" : " • "+item.product.amountValue+" "+item.product.amountUnit);
        } else {
            productTitle = item.product.brand+" • "+item.product.type+
                    (item.product.amountValue==-1 ? "" : " • "+item.product.amountValue+" "+item.product.amountUnit);
        }

        title.setText(productTitle);
        expandedTitle.setText(productTitle);
        shortDesc.setText(item.product.shortDesc);
        expandedShortDesc.setText(item.product.shortDesc);
        count.setText(item.count+"x");
        expandedCount.setText("x"+item.count);
        expiration.setText(item.expiration==null ? "" : item.displayDate);
        expandedExpiration.setText(item.expiration==null ? "" : item.displayDate);

        return row;

    }
}
