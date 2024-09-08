package com.example.sw0b_001;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppCompactActivityCustomized extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO: check if shared key is available else kill
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        implementViewSecurities(view);
        super.setContentView(view);
    }

    private void implementViewSecurities(View view) {
        view.setFilterTouchesWhenObscured(true);
    }

}
