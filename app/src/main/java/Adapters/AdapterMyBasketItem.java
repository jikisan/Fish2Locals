package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Basket;
import Models.Products;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterMyBasketItem extends RecyclerView.Adapter<AdapterMyBasketItem.ItemViewHolder>{

    private List<Basket> arrBasket = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;
    private DatabaseReference basketDatabase ;
    private FirebaseUser user;
    private String myUserId, databaseName;

    public AdapterMyBasketItem() {
    }

    public AdapterMyBasketItem(List<Basket> arrBasket, Context context) {
        this.arrBasket = arrBasket;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMyBasketItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_basket,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyBasketItem.ItemViewHolder holder, int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        Basket basket = arrBasket.get(position);

        String fishImageName = basket.getImageName();
        String productName = basket.getFishName();
        int productQuantity = basket.getQuantityByKilo();
        double productPrice = basket.getPricePerKilo();
        databaseName = myUserId + "-" + productName;

        int imageResource = context.getResources().getIdentifier(fishImageName, "drawable", context.getPackageName());

        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_productPhoto);

        holder.tv_productName.setText(productName);
        holder.tv_productQuantity.setText(productQuantity + " kilogram/s.");
        holder.tv_productPrice.setText("₱ " + productPrice + " / Kg");

        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");

        holder.iv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SweetAlertDialog sDialog;

                sDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Delete " +productName+ " ?");
                sDialog.setCancelText("Back");
                sDialog.setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog)
                            {

                                basketDatabase.child(databaseName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        snapshot.getRef().removeValue();
                                        int actualPosition = holder.getAbsoluteAdapterPosition();
                                        arrBasket.remove(actualPosition);
                                        notifyItemRemoved(actualPosition);
                                        sDialog.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });
                sDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrBasket.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_productPhoto, iv_deleteBtn;
        TextView tv_productName, tv_productQuantity, tv_productPrice;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);

            tv_productQuantity = itemView.findViewById(R.id.tv_productQuantity);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);

            iv_productPhoto = itemView.findViewById(R.id.iv_productPhoto);
            iv_deleteBtn = itemView.findViewById(R.id.iv_deleteBtn);


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
