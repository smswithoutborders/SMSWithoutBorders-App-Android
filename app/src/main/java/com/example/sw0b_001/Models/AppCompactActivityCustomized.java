package com.example.sw0b_001.Models;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.R;

import java.util.Locale;

public class AppCompactActivityCustomized extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO: check if shared key is available else kill
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setContentView(View view) {
        customizeViewForLanguage(view);
        implementViewSecurities(view);
        super.setContentView(view);
    }

    private void implementViewSecurities(View view) {
        view.setFilterTouchesWhenObscured(true);
    }

    private void customizeViewForLanguage(View view) {
//        updateLanguage();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the state of the SwitchPreferenceCompact
        String localeLanguage = prefs.getString("language_options", getString(R.string.language_english_value));
        Log.d(getLocalClassName(), "Locale language: " + localeLanguage);

//        if(new Locale(localeLanguage).equals(Locale.forLanguageTag("fa"))) {
//            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        }
    }

    private void updateLanguage() {
        // Get the SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();

        // Get the state of the SwitchPreferenceCompact
        String languageLocale = prefs.getString("language_options", languageCode);
        if(BuildConfig.DEBUG) {
            Log.d(getLocalClassName(), "Language code: " + languageCode);
            Log.d(getLocalClassName(), "Language locale: " + languageLocale);
        }

        LanguageHandler.updateLanguage(getResources(), languageLocale);
    }
}
