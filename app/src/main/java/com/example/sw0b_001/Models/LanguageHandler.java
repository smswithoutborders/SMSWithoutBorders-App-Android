package com.example.sw0b_001.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LanguageHandler {

    public static final String customSelectedLanguage = "CUSTOM_SELECTED_LANGUAGE";

    public static boolean hasPersistedData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(customSelectedLanguage);
    }

    public static String getPersistedData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(customSelectedLanguage, "en");
    }

    public static void persistLanguage(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(customSelectedLanguage, language);
        editor.commit();
        // editor.apply();
    }


    public static void updateLanguage(Resources resources, String language) {
        Locale locale = new Locale(language);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
