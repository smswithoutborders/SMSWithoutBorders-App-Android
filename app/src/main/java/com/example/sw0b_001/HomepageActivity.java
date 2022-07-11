package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.RecentsRecyclerAdapter;
import com.example.sw0b_001.SettingsActivities.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(bottomNavigationView);

        populateEncryptedMessages();
        setSearchListener();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        EditText searchEditText = findViewById(R.id.recent_search_edittext);
        searchEditText.clearFocus();
    }

    public void setSearchListener() {
        EditText searchEditText = findViewById(R.id.recent_search_edittext);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    List<EncryptedContent> encryptedContentList = fetchMessagesFromDatabase(charSequence.toString());
                    populateEncryptedMessages(encryptedContentList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void populateEncryptedMessages(List<EncryptedContent> encryptedContentList) {
        RecyclerView recentsRecyclerView = findViewById(R.id.recents_recycler_view);
        // recentsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        TextView noRecentMessagesText = findViewById(R.id.no_recent_messages);
        if(!encryptedContentList.isEmpty()) noRecentMessagesText.setVisibility(View.INVISIBLE);
        else noRecentMessagesText.setVisibility(View.VISIBLE);

        RecentsRecyclerAdapter recentsRecyclerAdapter = new RecentsRecyclerAdapter(this, encryptedContentList, R.layout.layout_cardlist_recents);
        recentsRecyclerView.setAdapter(recentsRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void populateEncryptedMessages() {
        RecyclerView recentsRecyclerView = findViewById(R.id.recents_recycler_view);
        // recentsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        try {
            List<EncryptedContent> encryptedContentList = fetchMessagesFromDatabase();

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

    private List<EncryptedContent> fetchMessagesFromDatabase(String filterText) throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(), Datastore.class,
                Datastore.DatabaseName).build();

        final List<EncryptedContent>[] encryptedContentList = new List[]{new ArrayList<>()};
        Thread fetchEncryptedMessagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EncryptedContentDAO encryptedContentDAO = databaseConnector.encryptedContentDAO();
                encryptedContentList[0] = encryptedContentDAO.getForFilterText(filterText);
            }
        });

        fetchEncryptedMessagesThread.start();
        fetchEncryptedMessagesThread.join();

        return encryptedContentList[0];
    }

    private List<EncryptedContent> fetchMessagesFromDatabase() throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(), Datastore.class,
                Datastore.DatabaseName)
                .build();

        final List<EncryptedContent>[] encryptedContentList = new List[]{new ArrayList<>()};
        Thread fetchEncryptedMessagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EncryptedContentDAO encryptedContentDAO = databaseConnector.encryptedContentDAO();
                encryptedContentList[0] = encryptedContentDAO.getAll();
            }
        });

        fetchEncryptedMessagesThread.start();
        fetchEncryptedMessagesThread.join();

        return encryptedContentList[0];
    }

    public void bottomNavBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.recents);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.settings: {
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout constraintLayout = findViewById(R.id.recent_messages_constrain);
        constraintLayout.setFocusable(true);

        populateEncryptedMessages();
    }
}
