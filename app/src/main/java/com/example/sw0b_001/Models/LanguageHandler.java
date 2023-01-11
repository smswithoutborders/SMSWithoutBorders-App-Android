package com.example.sw0b_001.Models;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageHandler {


    public static void updateLanguage(Resources resources, String language) {
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(language);
        config.setLocale(locale);

        Locale.setDefault(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }
}
