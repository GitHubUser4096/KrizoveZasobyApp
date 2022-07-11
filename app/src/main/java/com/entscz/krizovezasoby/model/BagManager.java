package com.entscz.krizovezasoby.model;

import android.provider.ContactsContract;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BagManager {

    private boolean bagsChanged;
    private ArrayList<Bag> bags;
    private HashMap<Integer, Integer> bagsIndex;
    private HashMap<Integer, BagInfo> bagInfos;

    public BagManager(){
        bags = new ArrayList<>();
        bagsIndex = new HashMap<>();
        bagInfos = new HashMap<>();
        bagsChanged = true;
    }

    public int createBag(String name) throws Requests.NetworkError, DataManager.ContentError, DataManager.APIError {

        try {

            JSONObject bag = new JSONObject(Requests.POST(DataManager.API_URL+"/bag/createBag.php", new Requests.Params()
                    .add("name", name)
                    .add("description", "")
            ).await());

            int bagId = bag.getInt("id");

            bagsChanged = true;

            return bagId;

        } catch(Requests.HTTPError e) {
            throw new DataManager.ContentError(e.message);
        } catch(JSONException e){
            throw new DataManager.APIError();
        }

    }

    public void deleteBag(int bagId) throws Requests.NetworkError, DataManager.ContentError {

        try {

            Requests.POST(DataManager.API_URL + "/bag/deleteBag.php?bagId=" + bagId, new Requests.Params()).await();
            bagsChanged = true;

        } catch(Requests.HTTPError e){
            throw new DataManager.ContentError(e.message);
        }

    }

    public Bag getBagById(int bagId) throws Requests.NetworkError, DataManager.APIError {
        if(bagsChanged) fetchBags();
        return bags.get(bagsIndex.get(bagId));
    }

    /** Updates the bag list if necessary and returns it. **/
    public ArrayList<Bag> getBags() throws Requests.NetworkError, DataManager.APIError {
        if(bagsChanged) fetchBags();
        return bags;
    }

    private BagInfo fetchBagInfo(int bagId) throws Requests.NetworkError, DataManager.APIError {
        try {
            JSONObject bagInfoJson = new JSONObject(Requests.GET(DataManager.API_URL+"/bag/getInfo.php?bagId="+bagId).await());
            String name = bagInfoJson.getString("name");
            String description = bagInfoJson.isNull("description") ? "" : bagInfoJson.getString("description");
            BagInfo bagInfo = new BagInfo(name, description);
            bagInfos.put(bagId, bagInfo);
            return bagInfo;
        } catch(JSONException | Requests.HTTPError e){
            throw new DataManager.APIError();
        }
    }

    public BagInfo getBagInfo(int bagId) throws Requests.NetworkError, DataManager.APIError {
        if(!bagInfos.containsKey(bagId)){
            fetchBagInfo(bagId);
        }
        return bagInfos.get(bagId);
    }

    public void updateBagInfo(int bagId, String name, String description) throws Requests.NetworkError, DataManager.ContentError {
        try {

            Requests.POST(DataManager.API_URL+"/bag/updateInfo.php?bagId="+bagId, new Requests.Params()
                    .add("name", name)
                    .add("description", description)
            ).await();

            bagsChanged = true;
            fetchBagInfo(bagId);

        } catch(Requests.HTTPError e){
            throw new DataManager.ContentError(e.message);
        }
    }

    private ArrayList<Bag> fetchBags() throws Requests.NetworkError, DataManager.APIError {

        try {

            JSONArray bagsJson = new JSONArray(Requests.GET(DataManager.API_URL+"/bag/listBags.php").await());

            bags.clear();
            bagsIndex.clear();

            for(int i = 0; i<bagsJson.length(); i++){
                JSONObject bagJson = bagsJson.getJSONObject(i);
                int id = bagJson.getInt("id");
                String name = bagJson.getString("name");
                String state = bagJson.getString("state");
                Bag bag = new Bag(id, name, state);
                bags.add(bag);
                bagsIndex.put(id, i);
            }

            bagsChanged = false;

            return bags;

        } catch (JSONException | Requests.HTTPError e){
            // TODO log the error (everywhere!)
            throw new DataManager.APIError();
        }

    }

}
