package com.bas.bandclient.storage;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Владимир on 23.05.2017.
 */

public class SharedPreferencesStorage implements IStorage {

    private Gson gson;
    private SharedPreferences sharedPreferences;

    public SharedPreferencesStorage(Gson gson, SharedPreferences sharedPreferences) {
        this.gson = gson;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public <T> T get(String key, Class<T> clz) {
        String json = sharedPreferences.getString(key, null);

        if (json == null)
            return null;

        return gson.fromJson(json, clz);
    }

    @Override
    public <T> T get(String key, Type clz) {
        String json = sharedPreferences.getString(key, null);

        if (json == null)
            return null;

        return gson.fromJson(json, clz);
    }

    @Override
    public void put(String key, Object obj) {
        String json = gson.toJson(obj);
        store(key, json);
    }

    @Override
    public void remove(String key) {
        store(key, null);
    }

    @Override
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void store(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
