package com.example.sw0b_001.Models;

import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public class LanguageHandler {

    public static void updateLanguage(Resources resources, String language) {
//        Configuration config = resources.getConfiguration();
//        Locale locale = new Locale(language);
//        config.setLocale(locale);
//
//        Locale.setDefault(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//
//        AppCompatDelegate.setApplicationLocales();

        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(language);
        // Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale);

    }
}
