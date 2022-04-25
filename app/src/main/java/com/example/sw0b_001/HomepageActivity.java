package com.example.sw0b_001;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(getApplicationContext(), this.getParent(), bottomNavigationView);
    }

    public static void bottomNavBar(Context context, Activity activity, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.platform);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//                Log.i(this.getClass().getSimpleName(), item.getTitle().toString());
                switch(item.getItemId()) {
                    case R.id.settings:
                        context.startActivity(new Intent(context, SettingsActivity.class));
                        activity.finish();
                }
                return false;
            }
        });
    }


    public void onClickPlatformSelect(View view) {
        Intent platformIntent = new Intent(getApplicationContext(), PlatformsActivity.class);
        startActivity(platformIntent);
    }
}
