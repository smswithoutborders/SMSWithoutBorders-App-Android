package com.example.sw0b_001.SettingsActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.sw0b_001.HomepageActivity;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivityLanguageSettingsBinding;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompactActivityCustomized {

    private String[] supportedLanguages = new String[]{"en", "fr", "fa"};

    private ActivityLanguageSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageSettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar gatewayClientToolbar = (Toolbar) findViewById(R.id.language_settings_toolbar);
        setSupportActionBar(gatewayClientToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        LanguageHandler.getPersistedData(getApplicationContext());

        populateLanguages();
    }


    @SuppressLint("ResourceType")
    private void populateLanguages() {
        RadioGroup group = (RadioGroup) findViewById(R.id.language_radio);
        for(int i=0;i<supportedLanguages.length;++i) {
            String supportedLanguage = supportedLanguages[i];
            Locale locale = Locale.forLanguageTag(supportedLanguage);

            RadioButton languageRadioButton = new RadioButton(this);
            languageRadioButton.setText(locale.getDisplayLanguage());
            languageRadioButton.setId(i);

            group.addView(languageRadioButton);

            Locale currentLocale = getResources().getConfiguration().locale;
            if(supportedLanguage.equals(currentLocale.getLanguage())) {
                languageRadioButton.toggle();
            }
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Resources resources = getResources();

                LanguageHandler.updateLanguage(resources, supportedLanguages[i]);
                LanguageHandler.persistLanguage(getApplicationContext(), supportedLanguages[i]);

                Intent languageIntent = new Intent(getApplicationContext(), HomepageActivity.class);
                startActivity(languageIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();
            }
        });
    }
}