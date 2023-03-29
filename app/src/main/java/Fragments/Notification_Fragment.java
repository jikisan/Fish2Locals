package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fish2locals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapters.AdapterChatItem;
import Adapters.AdapterNotificationItem;
import Models.Chats;
import Models.Notifications;

public class Notification_Fragment extends Fragment {

    private List<Notifications> arrNotifications = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText, tv_deleteBtn;
    private RecyclerView recyclerView_notification;

    private AdapterNotificationItem adapterNotificationItem;
    private FirebaseUser user;
    private DatabaseReference notifDatabase, messageDatabase;

    private String myUserId, userOne, userTwo, chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        notifDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();

        return view;
    }

    private void clickListeners() {

        tv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            Query query = notifDatabase.orderByChild("notificationUserId").equalTo(myUserId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        snapshot.getRef().removeValue();

                        int size = arrNotifications.size();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                arrNotifications.remove(0);
                            }

                            adapterNotificationItem.notifyItemRangeRemoved(0, size);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView_notification.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_notification.setLayoutManager(linearLayoutManager);

        adapterNotificationItem = new AdapterNotificationItem(arrNotifications, getContext());
        recyclerView_notification.setAdapter(adapterNotificationItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = notifDatabase.orderByChild("notificationUserId").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrNotifications.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Notifications notifications = dataSnapshot.getValue(Notifications.class);

                        arrNotifications.add(notifications);
                    }
                }

                if (arrNotifications.isEmpty()) {
                    recyclerView_notification.setVisibility(View.GONE);
                    tv_noMessagesText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    recyclerView_notification.setVisibility(View.VISIBLE);
                    tv_noMessagesText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }

                Collections.reverse(arrNotifications);
                progressBar.setVisibility(View.GONE);
                adapterNotificationItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_notification = view.findViewById(R.id.recyclerView_notification);

        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);
        tv_deleteBtn = view.findViewById(R.id.tv_deleteBtn);

    }
}