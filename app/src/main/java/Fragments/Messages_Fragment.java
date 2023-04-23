package Fragments;

import android.content.Intent;
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
import com.example.fish2locals.chat_activity_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterChatItem;
import Models.Chats;

public class Messages_Fragment extends Fragment {

    private List<Chats> arrChat = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText;
    private RecyclerView recyclerView_chatProfiles;
    private AdapterChatItem adapterChatItem;

    private FirebaseUser user;
    private DatabaseReference chatDatabase, messageDatabase;

    private String myUserID, userOne, userTwo, chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();

        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");

        setRef(view); // initialize UI Id's
        generateRecyclerLayout(); // generate recylcer layout
        clickListeners(); // buttons

        return view;
    }

    private void clickListeners() {

        adapterChatItem.setOnItemClickListener(new AdapterChatItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String clickedChatId = arrChat.get(position).getChatID();
                String storeId = arrChat.get(position).getStoreID();
                String currentUserOne;
                String currentUserTwo;
                String splitClickedChatId[] = clickedChatId.split("_");
                currentUserOne = splitClickedChatId[0];
                currentUserTwo = splitClickedChatId[1];
                String storeOwnersUserId = currentUserOne;

                if(currentUserOne.equals(myUserID))
                {
                    storeOwnersUserId = currentUserTwo;
                }

                String chatUid1 = currentUserOne + "_" + currentUserTwo;
                String chatUid2 = currentUserTwo + "_" + currentUserOne;

                Intent intent = new Intent(getActivity(), chat_activity_page.class);
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("storeId", storeId);
                intent.putExtra("chatUid1", chatUid1);
                intent.putExtra("chatUid2", chatUid2);
                intent.putExtra("userIdFromSearch", userTwo);
                intent.putExtra("needNotification", "1");
                startActivity(intent);
            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView_chatProfiles.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_chatProfiles.setLayoutManager(linearLayoutManager);

        arrChat = new ArrayList<>();
        adapterChatItem = new AdapterChatItem(arrChat);
        recyclerView_chatProfiles.setAdapter(adapterChatItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrChat.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Chats chats = dataSnapshot.getValue(Chats.class);

                        chatId = dataSnapshot.getKey().toString();
                        userOne = chats.getUserIdOne();
                        userTwo = chats.getUserIdTwo();

                        String tempChatId = userOne + "_" + myUserID;

                        if(chatId.equals(tempChatId))
                        {
                            arrChat.add(chats);
                        }

                    }
                }


                if (arrChat.isEmpty()) {
                    recyclerView_chatProfiles.setVisibility(View.GONE);
                    tv_noMessagesText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    recyclerView_chatProfiles.setVisibility(View.VISIBLE);
                    tv_noMessagesText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }

                adapterChatItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_chatProfiles = view.findViewById(R.id.recyclerView_chatProfiles);

        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);
    }

}