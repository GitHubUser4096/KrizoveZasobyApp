package com.entscz.krizovezasoby.model;

import com.entscz.krizovezasoby.util.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductManager {

    private HashMap<Integer, Product> productsById;
    private HashMap<String, Product> productsByCode;

    public ProductManager(){
        productsById = new HashMap<>();
        productsByCode = new HashMap<>();
    }

    public Product parseProduct(JSONObject productJson) throws DataManager.APIError {
        Product product;
        try {
            product = new Product(
                    productJson.getInt("id"),
                    productJson.getString("brand"),
                    productJson.getString("type"),
                    productJson.optInt("amountValue", -1),
                    productJson.getString("amountUnit"),
                    productJson.getString("shortDesc"),
                    productJson.getString("code"),
                    productJson.isNull("imgName") ? null : productJson.getString("imgName"),
                    productJson.getString("packageType"),
                    productJson.getString("description"));
        } catch(JSONException e){
            throw new DataManager.APIError();
        }
        productsById.put(product.id, product);
        productsByCode.put(product.code, product);
        return product;
    }

    public ArrayList<Product> searchProducts(String search) throws Requests.NetworkError, DataManager.APIError {
        ArrayList<Product> products = new ArrayList<>();
        try {
            JSONArray productsJson = new JSONArray(Requests.GET(DataManager.API_URL+"/product/searchProducts.php?search=" + search).await());
            for(int i = 0; i<productsJson.length(); i++){
                JSONObject productJson = productsJson.getJSONObject(i);
                products.add(parseProduct(productJson));
            }
        } catch(Requests.HTTPError | JSONException e){
            throw new DataManager.APIError();
        }
        return products;
    }

    public Product getProductById(int productId) throws Requests.NetworkError, DataManager.APIError, DataManager.ContentError {
        if(!productsById.containsKey(productId)) {
            try {
                JSONObject productJson = new JSONObject(Requests.GET(DataManager.API_URL+"/product/getProductById.php?productId=" + productId).await());
                parseProduct(productJson);
            } catch (JSONException e) {
                throw new DataManager.APIError();
            } catch(Requests.HTTPError e){
                throw new DataManager.ContentError(e.message);
            }
        }
        return productsById.get(productId);
    }

    public Product getProductByCode(String code) throws Requests.NetworkError, DataManager.APIError, DataManager.ContentError {
        if(!productsByCode.containsKey(code)) {
            try {
                JSONObject productJson = new JSONObject(Requests.GET(DataManager.API_URL+"/product/getProductByCode.php?code=" + code).await());
                parseProduct(productJson);
            } catch (JSONException e) {
                throw new DataManager.APIError();
            } catch(Requests.HTTPError e){
                throw new DataManager.ContentError(e.message);
            }
        }
        return productsByCode.get(code);
    }

    public Product createProduct(String brand, String productType, String amountValue, String amountUnit, String shortDesc, String code, String packageType, String description, String imgName) throws Requests.NetworkError, DataManager.ContentError, DataManager.APIError {
        try {
            JSONObject product = new JSONObject(Requests.POST(DataManager.API_URL+"/product/createProduct.php", new Requests.Params()
                    .add("brand", brand)
                    .add("type", productType)
                    .add("amountValue", amountValue)
                    .add("amountUnit", amountUnit)
                    .add("shortDesc", shortDesc)
                    .add("code", code)
                    .add("packageType", packageType)
                    .add("description", description)
                    .add("imgName", (imgName!=null ? imgName : ""))
            ).await());
            return parseProduct(product);
        } catch(Requests.HTTPError e) {
            throw new DataManager.ContentError(e.message);
        } catch(JSONException e){
            throw new DataManager.APIError();
        }
    }

}
