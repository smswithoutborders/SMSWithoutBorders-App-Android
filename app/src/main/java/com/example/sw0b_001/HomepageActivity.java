package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sw0b_001.HomepageFragments.AvailablePlatformsFragment;
import com.example.sw0b_001.HomepageFragments.NotificationsFragment;
import com.example.sw0b_001.HomepageFragments.RecentsFragment;
import com.example.sw0b_001.HomepageFragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

public class HomepageActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.recents);

        TextView textView = findViewById(R.id.fragment_title);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                textView.setVisibility(View.GONE);
                final int itemId = item.getItemId();
                switch(itemId) {
                    case R.id.recents: {
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                RecentsFragment.class, null)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }
                    case R.id.settings: {
                        textView.setText(R.string.settings_settings);
                        textView.setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                SettingsFragment.class, null)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }

                    case R.id.messages: {
                        textView.setText(R.string.messages_title);
                        textView.setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                        NotificationsFragment.class, null)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void onComposePlatformClick(View view) {
        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                        AvailablePlatformsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .commit();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SplitInstallManager splitInstallManager = SplitInstallManagerFactory.create(getBaseContext());
        final String[] supportedLanguages = getResources().getStringArray(R.array.language_values);
        final Set<String> installedLangs = splitInstallManager.getInstalledLanguages();

        for(String supportedLanguage : supportedLanguages) {
            if(installedLangs.contains(supportedLanguage))
                continue;

            SplitInstallRequest request =
                    SplitInstallRequest.newBuilder()
                            .addLanguage(Locale.forLanguageTag(supportedLanguage))
                            .build();
            splitInstallManager.startInstall(request);
            splitInstallManager.registerListener(new SplitInstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(@NonNull SplitInstallSessionState splitInstallSessionState) {
//                    if(splitInstallSessionState.status() == SplitInstallSessionStatus.INSTALLED){
//                    }
                }
            });
        }
    }
}