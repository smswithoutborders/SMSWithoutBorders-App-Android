package com.example.sw0b_001.Models;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        Locale locale = getResources().getConfiguration().locale;
        if(locale.equals(Locale.forLanguageTag("fa"))) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}
