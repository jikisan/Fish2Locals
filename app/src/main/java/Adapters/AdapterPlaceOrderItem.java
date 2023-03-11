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
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterPlaceOrderItem extends RecyclerView.Adapter<AdapterPlaceOrderItem.ItemViewHolder > {

    private List<Basket> arrBasket = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterPlaceOrderItem() {
    }

    public AdapterPlaceOrderItem(List<Basket> arrBasket, Context context) {
        this.arrBasket = arrBasket;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPlaceOrderItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_order_product_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlaceOrderItem.ItemViewHolder holder, int position) {

        Basket basket = arrBasket.get(position);


        String fishImageName = basket.getImageName();
        String productName = basket.getFishName();
        int productQuantity = basket.getQuantityByKilo();
        double productPrice = basket.getPricePerKilo();

        int imageResource = context.getResources().getIdentifier(fishImageName, "drawable", context.getPackageName());

        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_productPhoto);

        holder.tv_productName.setText(productName);
        holder.tv_productQty.setText(productQuantity + " Kg");
        holder.tv_productPrice.setText("₱ " + productPrice + " / Kg");

        boolean pickup = basket.isPickup();
        boolean ownDelivery = basket.isOwnDelivery();
        boolean thirdPartyDelivery = basket.isThirrdPartyDelivery();

        if(pickup == true)
        {
            holder.tv_deliveryOption.setText("Pickup");
        }
        else if(ownDelivery == true)
        {
            holder.tv_deliveryOption.setText("Store Own Delivery");
        }
        else if(thirdPartyDelivery == true)
        {
            holder.tv_deliveryOption.setText("3rd Part Delivery");
        }



    }

    @Override
    public int getItemCount() {
        return arrBasket.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_productPhoto;
        TextView tv_productName, tv_productQty, tv_productPrice, tv_deliveryOption;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_deliveryOption = itemView.findViewById(R.id.tv_deliveryOption);
            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productQty = itemView.findViewById(R.id.tv_productQty);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);
            iv_productPhoto = itemView.findViewById(R.id.iv_productPhoto);

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
