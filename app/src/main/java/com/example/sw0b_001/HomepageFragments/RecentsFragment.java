package com.example.sw0b_001.HomepageFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.Notifications.NotificationsHandler;
import com.example.sw0b_001.Models.RecentsRecyclerAdapter;
import com.example.sw0b_001.Models.RecentsViewModel;
import com.example.sw0b_001.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class RecentsFragment extends Fragment {
    // TODO: Implement search with LiveData
    RecentsViewModel recentsViewModel;
    EncryptedContentDAO encryptedContentDAO;

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            RecentsRecyclerAdapter recentsRecyclerAdapter = new RecentsRecyclerAdapter(getContext(), R.layout.layout_cardlist_recents);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);

            RecyclerView recentsRecyclerView = view.findViewById(R.id.recents_recycler_view);
            recentsRecyclerView.setLayoutManager(linearLayoutManager);
            recentsRecyclerView.setAdapter(recentsRecyclerAdapter);

            recentsViewModel = new ViewModelProvider(this).get(
                    RecentsViewModel.class );

            Datastore databaseConnector = Room.databaseBuilder(getContext(), Datastore.class,
                    Datastore.DatabaseName).build();

            encryptedContentDAO = databaseConnector.encryptedContentDAO();
            recentsViewModel.getMessages(encryptedContentDAO).observe(getViewLifecycleOwner(), new Observer<List<EncryptedContent>>() {
                @Override
                public void onChanged(List<EncryptedContent> encryptedContents) {
                    TextView noRecentMessagesText = view.findViewById(R.id.no_recent_messages);

                    if(!encryptedContents.isEmpty()) noRecentMessagesText.setVisibility(View.INVISIBLE);
                    else noRecentMessagesText.setVisibility(View.VISIBLE);

                    recentsRecyclerAdapter.submitList(encryptedContents);
                }
            });

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

}