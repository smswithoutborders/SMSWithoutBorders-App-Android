package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.RecentsRecyclerAdapter;
import com.example.sw0b_001.SettingsActivities.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        SearchView searchView = (SearchView) findViewById(R.id.search_bar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(bottomNavigationView);

        populateEncryptedMessages();
    }

    public void populateEncryptedMessages() {
        RecyclerView recentsRecyclerView = findViewById(R.id.recents_recycler_view);
        // recentsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        try {
            List<EncryptedContent> encryptedContentList = fetchMessagesFromDatabase();
            Log.d(getLocalClassName(), "# Encrypted content size: " + encryptedContentList.size());

            if(!encryptedContentList.isEmpty()) {
                TextView noRecentMessagesText = findViewById(R.id.no_recent_messages);
                noRecentMessagesText.setVisibility(View.INVISIBLE);
            }

            RecentsRecyclerAdapter recentsRecyclerAdapter = new RecentsRecyclerAdapter(this, encryptedContentList, R.layout.layout_cardlist_recents);
            recentsRecyclerView.setAdapter(recentsRecyclerAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);
            recentsRecyclerView.setLayoutManager(linearLayoutManager);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<EncryptedContent> fetchMessagesFromDatabase() throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(), Datastore.class,
                Datastore.DatabaseName).build();

        final List<EncryptedContent>[] encryptedContentList = new List[]{new ArrayList<>()};
        Thread fetchEmailMessagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EncryptedContentDAO encryptedContentDAO = databaseConnector.encryptedContentDAO();
                encryptedContentList[0] = encryptedContentDAO.getAll();
            }
        });

        // TODO: Fetch other platforms text from here
        fetchEmailMessagesThread.start();
        fetchEmailMessagesThread.join();

        return encryptedContentList[0];
    }

    public void bottomNavBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.recents);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.settings: {
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                        finish();
                    }
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
