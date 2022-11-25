package com.example.sw0b_001.Models;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AppCompactActivityRtlEnabled extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setContentView(View view) {
        Locale locale = getResources().getConfiguration().locale;
        if(locale.equals(Locale.forLanguageTag("fa")))
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.setContentView(view);
    }
}
