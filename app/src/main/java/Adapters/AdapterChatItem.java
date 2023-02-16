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

import Models.Chats;
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

        Chats chats = arr.get(position);



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

        ImageView iv_chatProfilePhoto, iv_deleteChat;
        TextView tv_category, tv_chatName;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_chatProfilePhoto = itemView.findViewById(R.id.iv_chatProfilePhoto);
            iv_deleteChat = itemView.findViewById(R.id.iv_deleteChat);
            tv_chatName = itemView.findViewById(R.id.tv_chatName);

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
