package com.example.sw0b_001.HomepageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.Notifications.Notifications;
import com.example.sw0b_001.Models.Notifications.NotificationsDAO;
import com.example.sw0b_001.Models.Notifications.NotificationsRecyclerAdapter;
import com.example.sw0b_001.Models.Notifications.NotificationsViewModel;
import com.example.sw0b_001.R;

import java.util.List;

public class NotificationsFragment extends Fragment {

    NotificationsViewModel notificationsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        NotificationsRecyclerAdapter notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(getContext(), R.layout.layout_cardlist_notifications);

        RecyclerView notificationsRecyclerView = view.findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(linearLayoutManager);
        notificationsRecyclerView.setAdapter(notificationsRecyclerAdapter);


        notificationsViewModel = new ViewModelProvider(this).get(
                NotificationsViewModel.class );

        try {
            TextView noRecentNotifications = view.findViewById(R.id.no_recent_notifications);

            Datastore databaseConnector = Room.databaseBuilder(getContext(), Datastore.class,
                    Datastore.databaseName).build();

            NotificationsDAO notificationsDAO = databaseConnector.notificationsDAO();
            notificationsViewModel.getNotifications(notificationsDAO).observe(getViewLifecycleOwner(), new Observer<List<Notifications>>() {
                @Override
                public void onChanged(List<Notifications> notificationsList) {
                    if(!notificationsList.isEmpty()) noRecentNotifications.setVisibility(View.INVISIBLE);
                    else noRecentNotifications.setVisibility(View.VISIBLE);

                    notificationsRecyclerAdapter.submitList(notificationsList);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}