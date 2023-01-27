package com.example.sw0b_001.HomepageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter;
import com.example.sw0b_001.R;

import java.util.List;

public class AvailablePlatformsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        List<Platform> platforms = PlatformsHandler.getAllPlatforms(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.list_synced_platforms);
        PlatformsRecyclerAdapter platformsRecyclerAdapter;

//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        platformsRecyclerAdapter = new PlatformsRecyclerAdapter(getContext(), platforms, R.layout.layout_cardlist_platforms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(platformsRecyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_platforms, container, false);
    }
}