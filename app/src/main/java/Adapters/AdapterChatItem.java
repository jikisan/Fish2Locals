package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Chat;
import Models.Chats;
import Models.Store;
import Models.Users;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterChatItem extends RecyclerView.Adapter<AdapterChatItem.ItemViewHolder> {

    private List<Chats> arr;
    private OnItemClickListener onItemClickListener;
    private DatabaseReference userDatabase ;
    private FirebaseUser user;
    private TextModifier textModifier = new TextModifier();

    public AdapterChatItem() {
    }

    public AdapterChatItem(List<Chats> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public AdapterChatItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterChatItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_profile,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChatItem.ItemViewHolder holder, int position) {

        Chats chat = arr.get(position);

        String userOne = chat.getUserIdOne();
        String userTwo = chat.getUserIdTwo();
        String chatId = chat.getChatID();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = user.getUid();
        String userID;

        if(!userOne.equals(myUserID))
        {
            userID = userOne;
        }
        else
        {
            userID = userTwo;
        }

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);


                if(users != null){

                    String imageUrl = users.getImageUrl();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();

                    holder.tv_userName.setText(fname + " " + lname);


                    Picasso.get()
                            .load(imageUrl)
                            .into(holder.iv_userPhoto);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.iv_deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove message?")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                chatDatabase.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {

                                            dataSnapshot.getRef().removeValue();


                                        }

                                        deleteMessages(chatId);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            private void deleteMessages(String chatId) {

                                DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

                                Query query = messageDatabase
                                        .orderByChild("chatUid")
                                        .equalTo(chatId);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            dataSnapshot.getRef().removeValue();
                                        }
//
//                                        Toast.makeText(view.getContext(), "Chat Removed", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(view.getContext(), homepage.class);
//                                        view.getContext().startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setContentText("Remove this in the chat?")
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userPhoto, iv_deleteChat;
        TextView tv_category, tv_userName;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);
            iv_deleteChat = itemView.findViewById(R.id.iv_deleteChat);
            tv_userName = itemView.findViewById(R.id.tv_userName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }

                }
            });

        }
    }
}
