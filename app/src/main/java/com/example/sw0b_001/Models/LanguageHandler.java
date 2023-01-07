package com.example.sw0b_001.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LanguageHandler {

    public static void updateLanguage(Resources resources, String language) {
        Locale locale = new Locale(language);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
