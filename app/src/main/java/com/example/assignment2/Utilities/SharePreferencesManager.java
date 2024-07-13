package com.example.assignment2.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharePreferencesManager {

    private static SharePreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private SharePreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences("GameRecords", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static SharePreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharePreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public List<ScoreData> saveRecords(List<ScoreData> records) {
        String json = gson.toJson(records);
        sharedPreferences.edit().putString("records", json).apply();
        return records;
    }

    public List<ScoreData> getRecords() {
        String json = sharedPreferences.getString("records", null);
        Type type = new TypeToken<ArrayList<ScoreData>>() {}.getType();
        List<ScoreData> records = gson.fromJson(json, type);
        if (records == null) {
            records = new ArrayList<>();
        }
        return records;
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}
