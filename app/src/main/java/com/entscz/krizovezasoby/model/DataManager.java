package com.entscz.krizovezasoby.model;

import android.content.Context;

import com.entscz.krizovezasoby.Config;
import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {

//    public static final String BASE_URL = "https://zasoby.nggcv.cz";
//    public static final String API_URL = BASE_URL+"/api";
//    public static final String IMG_URL = BASE_URL+"/images";

    public static final String BASE_URL = Config.get().url;
    public static final String API_URL = BASE_URL+"/api";
    public static final String IMG_URL = BASE_URL+"/images";

    public static class ContentError extends RuntimeException {
        public ContentError(String message){
            super(message);
        }
    }

    public static class APIError extends RuntimeException {
        public APIError(){
            super("Chyba serveru!");
        }
    }

    private static DataManager instance;

    public static BagManager bags;
    public static ProductManager products;
    public static ItemManager items;

    static {
        instance = new DataManager();
    }

    public static void init(Context context){

        bags = new BagManager();
        products = new ProductManager();
        items = new ItemManager(context);

    }

    public static DataManager get(){
        return instance;
    }

//    private ArrayList<Bag> tmpBags;
//    private HashMap<Integer, Integer> bagsIndex;
//    private HashMap<Integer, Product> tmpProducts;
//    private HashMap<String, Product> productCodeIndex;
//    private HashMap<Integer, ArrayList<Item>> tmpItems;
//    private HashMap<Integer, Item> itemIndex;
    private ArrayList<CharityPlace> charityPlaces;

    private DataManager(){
//        tmpBags = new ArrayList<>();
//        bagsIndex = new HashMap<>();
//        tmpProducts = new HashMap<>();
//        productCodeIndex = new HashMap<>();
//        tmpItems = new HashMap<>();
//        itemIndex = new HashMap<>();
    }

//    @Deprecated
//    public int createBag(String name) {
//
//        try {
//
//            JSONObject bag = new JSONObject(Requests.POST(API_URL+"/bag/createBag.php", new Requests.Params()
//                    .add("name", name)
//                    .add("description", "")
//            ).await());
//
//            int bagId = bag.getInt("id");
//
//            // TODO more efficient?
//            fetchBags();
//
//            return bagId;
//
//        } catch(Requests.HTTPError e) {
//            throw new ContentError(e.message);
//        } catch(Requests.NetworkError e){
//            throw e;
//        } catch(JSONException e){
////            throw new RuntimeException(e);
//            throw new APIError();
//        }
//
//    }
//
//    @Deprecated
//    public Bag getBagById(int bagId){
//        return tmpBags.get(bagsIndex.get(bagId));
//    }
//
//    /** Returns the cached bags. Use when bags are unchanged since previous fetch. **/
//    @Deprecated
//    public ArrayList<Bag> getBags(){
//        return tmpBags;
//    }
//
//    /** Fetches bags from the server, updates the cache and returns them. Use when bags have changed since previous fetch. **/
//    @Deprecated
//    public ArrayList<Bag> fetchBags(){
//
//        try {
//
//            JSONArray bagsJson = new JSONArray(Requests.GET(API_URL+"/bag/listBags.php").await());
//
//            tmpBags.clear();
//            bagsIndex.clear();
//
//            for(int i = 0; i<bagsJson.length(); i++){
//                JSONObject bagJson = bagsJson.getJSONObject(i);
//                int id = bagJson.getInt("id");
//                String name = bagJson.getString("name");
//                String state = bagJson.getString("state");
//                Bag bag = new Bag(id, name, state);
//                tmpBags.add(bag);
//                bagsIndex.put(id, i);
//            }
//
//        } catch (Exception e){
//            throw new RuntimeException(e.getMessage());
//        }
//
//        return tmpBags;
//    }
//
//    @Deprecated
//    private Product parseProduct(JSONObject productJson){
//        Product product;
//        try {
//            product = new Product(
//                    productJson.getInt("id"),
//                    productJson.getString("brand"),
//                    productJson.getString("type"),
//                    productJson.optInt("amountValue", -1),
//                    productJson.getString("amountUnit"),
//                    productJson.getString("shortDesc"),
//                    productJson.getString("code"),
//                    productJson.isNull("imgName") ? null : productJson.getString("imgName"),
//                    productJson.getString("packageType"),
//                    productJson.getString("description"));
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }
//        tmpProducts.put(product.id, product);
//        productCodeIndex.put(product.code, product);
//        return product;
//    }
//
//    @Deprecated
//    public ArrayList<Product> searchProducts(String search){
//        ArrayList<Product> products = new ArrayList<>();
//        try {
//            JSONArray productsJson = new JSONArray(Requests.GET(API_URL+"/product/searchProducts.php?search=" + search).await());
//            for(int i = 0; i<productsJson.length(); i++){
//                JSONObject productJson = productsJson.getJSONObject(i);
//                products.add(parseProduct(productJson));
//            }
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }
//        return products;
//    }
//
//    @Deprecated
//    public Product getProductById(int productId){
//        if(!tmpProducts.containsKey(productId)) {
//            try {
//                JSONObject productJson = new JSONObject(Requests.GET(API_URL+"/product/getProductById.php?productId=" + productId).await());
//                parseProduct(productJson);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return tmpProducts.get(productId);
//    }
//
//    @Deprecated
//    public Product getProductByCode(String code){
//        if(!productCodeIndex.containsKey(code)) {
//            try {
//                JSONObject productJson = new JSONObject(Requests.GET(API_URL+"/product/getProductByCode.php?code=" + code).await());
//                parseProduct(productJson);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return productCodeIndex.get(code);
//    }
//
//    @Deprecated
//    public Product createProduct(String brand, String productType, String amountValue, String amountUnit, String shortDesc, String code, String packageType, String description, String imgName){
//        try {
//            JSONObject product = new JSONObject(Requests.POST(API_URL+"/product/createProduct.php", new Requests.Params()
//                    .add("brand", brand)
//                    .add("type", productType)
//                    .add("amountValue", amountValue)
//                    .add("amountUnit", amountUnit)
//                    .add("shortDesc", shortDesc)
//                    .add("code", code)
//                    .add("packageType", packageType)
//                    .add("description", description)
//                    .add("imgName", (imgName!=null ? imgName : ""))
//            ).await());
//            return parseProduct(product);
//        } catch(Requests.HTTPError e) {
//            throw new ContentError(e.message);
//        } catch(Requests.NetworkError e){
//            throw e;
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Deprecated
//    public ArrayList<Item> getItems(int bagId){
//        if(!tmpItems.containsKey(bagId)){
//            fetchItems(bagId);
//        }
//        return tmpItems.get(bagId);
//    }
//
//    @Deprecated
//    public Item getItemById(int itemId){
//        // TODO?
////        if(!itemIndex.containsKey(itemId)){
////
////        }
//        return itemIndex.get(itemId);
//    }
//
//    @Deprecated
//    public ArrayList<Item> fetchItems(int bagId){
//
//        try {
//
//            JSONArray itemsJson = new JSONArray(Requests.GET(API_URL+"/item/getItems.php?bagId="+bagId).await());
//
//            ArrayList<Item> itemList = new ArrayList<>();
//
//            for(int i = 0; i<itemsJson.length(); i++){
//                JSONObject itemJson = itemsJson.getJSONObject(i);
////                Product product = getProductById(itemJson.getInt("productId"));
//                Product product = parseProduct(itemJson.getJSONObject("product"));
//                Item item = new Item(
//                        itemJson.getInt("id"),
//                        product,
//                        itemJson.getInt("count"),
//                        itemJson.isNull("expiration") ? null : itemJson.getString("expiration"),
//                        itemJson.optString("displayDate"),
//                        itemJson.optString("useIn"),
//                        itemJson.getInt("used")>0,
//                        itemJson.getString("state"));
////                itemNames[i] = items.getJSONObject(i).getJSONObject("product").getString("shortDesc")
////                        +" x" +items.getJSONObject(i).getInt("count")
////                        +" - "+items.getJSONObject(i).getString("expiration");
//                itemList.add(item);
//                itemIndex.put(item.id, item);
//            }
//
////            adapter.setItems(items);
//
////            invalidateOptionsMenu();
//
////            adapter.addAll(itemNames);
//
//            tmpItems.put(bagId, itemList);
//
//            return itemList;
//
//        } catch (Exception e){
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @Deprecated
//    public Item findItem(int bagId, int productId, String expiration, boolean used){
//        String searchExp = expiration==null ? "" : expiration;
//        for(Item item: getItems(bagId)){
//            if(item.used!=used) continue;
//            if(item.product.id!=productId) continue;
////            Log.i("zasoby", item.expiration+" "+expiration);
////            if(item.expiration==null || expiration==null){
////                if(item.expiration==null && expiration==null) return item;
////                continue;
////            }
////            if(!(item.expiration==expiration || item.expiration.equals(expiration))) continue;
////            if(item.product.id==productId && (item.expiration==null))
////            if(!item.expiration.equals(expiration)) continue;
//            String itemExp = item.expiration==null ? "" : item.expiration;
//            if(!itemExp.equals(searchExp)) continue;;
//            return item;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public BagInfo fetchBagInfo(int bagId){
//        try {
//            JSONObject bagInfo = new JSONObject(Requests.GET(API_URL+"/bag/getInfo.php?bagId="+bagId).await());
//            String name = bagInfo.getString("name");
//            String description = bagInfo.isNull("description") ? "" : bagInfo.getString("description");
//            return new BagInfo(name, description);
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }
//    }

    // TODO move this somewhere else? (CharityPlaceManager or CharityPlace?)
    public ArrayList<CharityPlace> getCharityPlaces() throws Requests.NetworkError, APIError {
        if(charityPlaces==null){
//            charityPlaces = new ArrayList<>();
            try {

                ArrayList<CharityPlace> tmpPlaces = new ArrayList<>();

                JSONArray placesJson = new JSONArray(Requests.GET(API_URL+"/charity/listPlaces.php").await());

                for(int i = 0; i<placesJson.length(); i++){
                    JSONObject placeJson = placesJson.getJSONObject(i);
                    String charityName = placeJson.getJSONObject("charity").getString("name");
                    String street = placeJson.getString("street");
                    String place = placeJson.getString("place");
                    String postCode = placeJson.getString("postCode");
                    String openHours = placeJson.getString("openHours");
                    String contacts = placeJson.getJSONObject("charity").getString("contacts") +
                            (placeJson.isNull("contacts") ? "" : "\n"+placeJson.getString("contacts"));
                    CharityPlace charityPlace = new CharityPlace(charityName, street, place, postCode, openHours, contacts);
//                    charityPlaces.add(charityPlace);
                    tmpPlaces.add(charityPlace);
                }

                charityPlaces = tmpPlaces;

            } catch(JSONException | Requests.HTTPError e){
//                charityPlaces = null;
                throw new APIError();
            }
        }
        return charityPlaces;
    }

    // TODO move this somewhere else? (SettingsManager or Settings?)
    public Settings fetchSettings() throws Requests.NetworkError, APIError {
        try {

            JSONObject settings = new JSONObject(Requests.GET(API_URL+"/user/getSettings.php").await());

            String[] criticalTime = settings.optString("criticalTime").split(" ");
            String criticalValue = criticalTime[0];
            String criticalUnit = criticalTime[1];

            String[] warnTime = settings.optString("warnTime").split(" ");
            String warnValue = warnTime[0];
            String warnUnit = warnTime[1];

            String[] recommendedTime = settings.optString("recommendedTime").split(" ");
            String recommendedValue = recommendedTime[0];
            String recommendedUnit = recommendedTime[1];

            String dateFormat = settings.optString("dateFormat");

            boolean sendNotifs = settings.optInt("sendNotifs")>0;

            return new Settings(criticalValue, criticalUnit, warnValue, warnUnit, recommendedValue, recommendedUnit, dateFormat, sendNotifs);

        } catch(JSONException | Requests.HTTPError e){
            throw new APIError();
        }
    }

}
