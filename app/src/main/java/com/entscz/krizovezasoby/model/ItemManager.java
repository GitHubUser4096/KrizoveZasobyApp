package com.entscz.krizovezasoby.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.InspectableProperty;
import androidx.annotation.IntDef;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ItemManager {

    // TODO is this worth it? - add to itemDisplay or remove
    @IntDef({SORT_DATE, SORT_NAME})
    public @interface ItemSort {}

    public static final int SORT_DATE = 0;
    public static final int SORT_NAME = 1;
    public static final int DISPLAY_BRAND_FIRST = 0;
    public static final int DISPLAY_TYPE_FIRST = 1;

    private SharedPreferences prefs;

    @ItemSort
    private int itemSort = SORT_DATE;
    private int itemDisplay = DISPLAY_BRAND_FIRST;

    private HashMap<Integer, ArrayList<Item>> items;
    private HashMap<Integer, Item> itemIndex;

    public ItemManager(Context context){

        items = new HashMap<>();
        itemIndex = new HashMap<>();

        prefs = context.getSharedPreferences("itemPrefs", Context.MODE_PRIVATE);

        itemSort = prefs.getInt("sort", 0);
        itemDisplay = prefs.getInt("display", 0);

    }

    public void setSort(@ItemSort int sort){
        itemSort = sort;
        prefs.edit().putInt("sort", itemSort).apply();
    }

    public void setDisplay(int display){
        itemDisplay = display;
        prefs.edit().putInt("display", itemDisplay).apply();
    }

    public int getSort(){
        return itemSort;
    }

    public int getDisplay(){
        return itemDisplay;
    }

    public ArrayList<Item> getItems(int bagId) throws Requests.NetworkError, DataManager.APIError {
        if(!items.containsKey(bagId)){
            fetchItems(bagId);
        }
        Collections.sort(items.get(bagId), (a, b)->{
            if(itemSort==SORT_NAME){
                // if only one item is used, put it after the unused one (used items will be below unused)
                if(a.used!=b.used){
                    return a.used ? 1 : -1;
                }
                // if both dates are either used or unused, sort them by name
                return a.product.getTitle().compareTo(b.product.getTitle());
            } else {
                String aStrExp = a.expiration==null ? "" : a.expiration;
                String bStrExp = b.expiration==null ? "" : b.expiration;
                // if only one item is used, put it after the unused one (used items will be below unused)
                if(a.used!=b.used){
                    return a.used ? 1 : -1;
                }
                // if only one item has a date, put it before the other one (items with a date will be above those without)
                if(Math.signum(aStrExp.length())!=Math.signum(bStrExp.length())){
                    return aStrExp.length()>0 ? -1 : 1;
                }
                // if neither item has date, consider them equal (don't change their order)
                if(aStrExp.length()==0 && bStrExp.length()==0) {
                    return 0;
                }
                try {
                    // if both items have a date and both are either used or not used, sort them by date
                    Date aDate = new SimpleDateFormat("yyyy-MM-dd").parse(aStrExp);
                    Date bDate = new SimpleDateFormat("yyyy-MM-dd").parse(bStrExp);
                    return aDate.compareTo(bDate);
                } catch(Exception e){
                    // if parsing date fails, consider items equal (should not happen - item format is checked)
                    return 0;
                }
            }
        });
        ArrayList<Item> itemList = items.get(bagId);
        return itemList;
    }

    public Item getItemById(int itemId){
        return itemIndex.get(itemId);
    }

    public Item findItem(int bagId, int productId, String expiration, boolean used) throws Requests.NetworkError, DataManager.APIError {
        String searchExp = expiration==null ? "" : expiration;
        for(Item item: getItems(bagId)){
            if(item.used!=used) continue;
            if(item.product.id!=productId) continue;
            String itemExp = item.expiration==null ? "" : item.expiration;
            if(!itemExp.equals(searchExp)) continue;;
            return item;
        }
        return null;
    }

    public void fetchItems(int bagId) throws Requests.NetworkError, DataManager.APIError {

        try {

            JSONArray itemsJson = new JSONArray(Requests.GET(DataManager.API_URL+"/item/getItems.php?bagId="+bagId).await());

            ArrayList<Item> itemList = new ArrayList<>();

            for(int i = 0; i<itemsJson.length(); i++){
                JSONObject itemJson = itemsJson.getJSONObject(i);
                Product product = DataManager.products.parseProduct(itemJson.getJSONObject("product"));
                Item item = new Item(
                        itemJson.getInt("id"),
                        product,
                        itemJson.getInt("count"),
                        itemJson.isNull("expiration") ? null : itemJson.getString("expiration"),
                        itemJson.optString("displayDate"),
                        itemJson.optString("useIn"),
                        itemJson.getInt("used")>0,
                        itemJson.getString("state"));
                itemList.add(item);
                itemIndex.put(item.id, item);
            }

            items.put(bagId, itemList);

        } catch (JSONException | Requests.HTTPError e){
            throw new DataManager.APIError();
        }

    }

}
