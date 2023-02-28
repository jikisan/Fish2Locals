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
    private DatabaseReference storeDatabase ;
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

        Chats chats = arr.get(position);

        String storeId = chats.getStoreID();
        String chatId = chats.getChatID();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        storeDatabase.child(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Store store = snapshot.getValue(Store.class);

                if(snapshot.exists()){

                    String storeUrl = store.getStoreUrl();
                    String storeName = store.getStoreName();

                    holder.tv_storeName.setText(storeName);


                    Picasso.get()
                            .load(storeUrl)
                            .into(holder.iv_storePhoto);

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

        ImageView iv_storePhoto, iv_deleteChat;
        TextView tv_category, tv_storeName;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_storePhoto = itemView.findViewById(R.id.iv_storePhoto);
            iv_deleteChat = itemView.findViewById(R.id.iv_deleteChat);
            tv_storeName = itemView.findViewById(R.id.tv_storeName);

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
