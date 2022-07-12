package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sw0b_001.R;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        populateLanguages();
    }


    private void populateLanguages() {

        RadioGroup group = (RadioGroup) findViewById(R.id.language_radio);

        RadioButton enLanguageRadioButton = new RadioButton(this);
        enLanguageRadioButton.setId(0);
        enLanguageRadioButton.setText(R.string.settings_language_supported_language_en);

        RadioButton frLanguageRadioButton = new RadioButton(this);
        frLanguageRadioButton.setId(1);
        frLanguageRadioButton.setText(R.string.settings_language_supported_language_fr);

        group.addView(enLanguageRadioButton);
        group.addView(frLanguageRadioButton);

        Intent intent = getIntent();

        String defaultLanguage = "";
        if(intent.hasExtra("custom_language"))
            defaultLanguage = intent.getStringExtra("custom_language");
        else
            defaultLanguage = Locale.getDefault().getLanguage();

        switch (defaultLanguage) {
            case "en":
                enLanguageRadioButton.setChecked(true);
                break;

            case "fr":
                frLanguageRadioButton.setChecked(true);
                break;
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                String customLanguage = "";
                switch(i) {
                    case 0:
                        customLanguage = "en";
                        break;

                    case 1:
                        customLanguage = "fr";
                        break;
                }

                Locale locale = new Locale(customLanguage);

                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();

                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;

                resources.updateConfiguration(configuration, displayMetrics);

                Intent languageIntent = new Intent(getApplicationContext(), LanguageSettingsActivity.class);
                languageIntent.putExtra("custom_language", customLanguage);
                startActivity(languageIntent);
                finish();
            }
        });
    }
}