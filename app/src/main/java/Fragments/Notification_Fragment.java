package Fragments;

import android.os.Bundle;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterChatItem;
import Models.Chats;

public class Notification_Fragment extends Fragment {

    private List<String> arrNotifications = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText;
    private RecyclerView recyclerView_notification;

    private FirebaseUser user;
    private DatabaseReference notifDatabase, messageDatabase;

    private String userID, userOne, userTwo, chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        notifDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();

        return view;
    }

    private void clickListeners() {
    }

    private void generateRecyclerLayout() {

//        recyclerView_notification.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        recyclerView_notification.setLayoutManager(linearLayoutManager);
//
//        arrNotifications = new ArrayList<>();

        getViewHolderValues();

    }

    private void getViewHolderValues() {

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
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_notification = view.findViewById(R.id.recyclerView_notification);

        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);

    }
}