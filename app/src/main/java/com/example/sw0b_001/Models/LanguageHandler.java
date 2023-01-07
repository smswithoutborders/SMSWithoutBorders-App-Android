package com.example.sw0b_001.Models;

import android.app.LocaleConfig;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LanguageHandler {


    public static boolean updateLanguage(Resources resources, String language) {
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(language);
        config.setLocale(locale);

        Locale.setDefault(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        return true;
    }
}
