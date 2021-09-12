package com.technorizen.omniser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLocations;
import com.technorizen.omniser.models.ModelLogin;

import java.util.HashMap;

public class SharedPref {

    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor myPrefsEditor;
    public static SharedPref myObj;

    public SharedPref() {}

    public void setlanguage(String key,String value) {
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        myPrefEditor.putString(key,value);
        myPrefEditor.commit();
    }

    public String getLanguage(String key) {
        return myPrefs.getString(key,"");
    }

    public static SharedPref getInstance(Context context) {

        if (myObj == null) {
            myObj = new SharedPref();
            myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            myPrefsEditor = myPrefs.edit();
        }

        return myObj;

    }

    public void setBooleanValue(String key, boolean value){
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        myPrefEditor.putBoolean(key,value);
        myPrefEditor.commit();
    }

    public boolean getBooleanValue(String key) {
        return myPrefs.getBoolean(key,false);
    }



     public void setUserDetails(String Key, ModelLogin loginModel){
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginModel);
        myPrefEditor.putString(Key,json);
        myPrefEditor.commit();
    }

    public ModelLogin getUserDetails(String Key) {
        Gson gson = new Gson();
        String json = myPrefs.getString(Key,"");
        ModelLogin obj = gson.fromJson(json,ModelLogin.class);
        return obj;
    }

    public void setDevLocation(String Key,String value) {
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        myPrefEditor.putString(Key,value);
        myPrefEditor.commit();
    }

    public String getDevLocation(String Key) {
        String value = myPrefs.getString(Key,"");
        return value;
    }

    public void setCartHash(String Key, HashMap<String,ModelResTypeItems.Result.Item_data> cartHash) {
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartHash);
        myPrefEditor.putString(Key,json);
        myPrefEditor.commit();
    }

    public HashMap<String,ModelResTypeItems.Result.Item_data> getCartHash(String Key) {
        Gson gson = new Gson();
        String json = myPrefs.getString(Key,"");
        HashMap<String,ModelResTypeItems.Result.Item_data> obj = gson.fromJson(json,HashMap.class);
        return obj;
    }

    public void setRestaurantModel(String Key, ModelResTypeItems modelResTypeItems){
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(modelResTypeItems);
        myPrefEditor.putString(Key,json);
        myPrefEditor.commit();
    }

    public ModelResTypeItems getRestaurantModel(String Key) {
        Gson gson = new Gson();
        String json = myPrefs.getString(Key,"");
        ModelResTypeItems obj = gson.fromJson(json,ModelResTypeItems.class);
        return obj;
    }

    public void setLocationModel(String Key, ModelLocations modelResTypeItems) {
        SharedPreferences.Editor myPrefEditor = myPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(modelResTypeItems);
        myPrefEditor.putString(Key,json);
        myPrefEditor.commit();
    }

    public ModelLocations getLocationModel(String Key) {
        Gson gson = new Gson();
        String json = myPrefs.getString(Key,"");
        ModelLocations obj = gson.fromJson(json,ModelLocations.class);
        return obj;
    }

    public void clearAllPreferences(){
        myPrefsEditor = myPrefs.edit();
        myPrefsEditor.clear();
        myPrefsEditor.commit();
    }

    public void clearPreference(String Key) {
        myPrefsEditor.remove(Key);
        myPrefsEditor.commit();
    }


}
