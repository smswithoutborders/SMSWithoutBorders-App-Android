package com.example.sw0b_001.SettingsActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.R;

public class LanguageSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        LanguageHandler.getPersistedData(getApplicationContext());

        populateLanguages();
    }


    @SuppressLint("ResourceType")
    private void populateLanguages() {
        RadioButton enLanguageRadioButton = new RadioButton(this);
        enLanguageRadioButton.setText(R.string.settings_language_supported_language_en);

        RadioButton frLanguageRadioButton = new RadioButton(this);
        frLanguageRadioButton.setText(R.string.settings_language_supported_language_fr);

        enLanguageRadioButton.setId(0);
        frLanguageRadioButton.setId(1);

        final boolean[] isCustomChecked = {false};

        if(LanguageHandler.hasPersistedData(getApplicationContext())) {
            switch(LanguageHandler.getPersistedData(getApplicationContext())) {
                case "en": {
                    enLanguageRadioButton.setChecked(true);
                    isCustomChecked[0] = true;
                    break;
                }

                case "fr": {
                    frLanguageRadioButton.setChecked(true);
                    isCustomChecked[0] = true;
                    break;
                }
            }
        }

        RadioGroup group = (RadioGroup) findViewById(R.id.language_radio);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d(getLocalClassName(), "** language ID: " + i);

                if(isCustomChecked[0]) {

                    isCustomChecked[0] = false;

                    return;
                }

                String customLanguage = "";
                switch(i) {
                    case 0:
                        customLanguage = "en";
                        break;

                    case 1:
                        customLanguage = "fr";
                        break;
                }

                Resources resources = getResources();

                LanguageHandler.updateLanguage(resources, customLanguage);

                LanguageHandler.persistLanguage(getApplicationContext(), customLanguage);

                Intent languageIntent = new Intent(getApplicationContext(), LanguageSettingsActivity.class);

                startActivity(languageIntent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();
            }
        });

        group.addView(enLanguageRadioButton);
        group.addView(frLanguageRadioButton);
    }
}