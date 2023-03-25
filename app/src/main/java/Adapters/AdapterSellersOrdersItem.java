package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.example.fish2locals.order_details_page;
import com.example.fish2locals.seller_order_details_page;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Orders;

public class AdapterSellersOrdersItem extends RecyclerView.Adapter<AdapterSellersOrdersItem.ItemViewHolder> {

    private List<Orders> arrOrders = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterSellersOrdersItem() {
    }

    public AdapterSellersOrdersItem(List<Orders> arrOrders, Context context) {
        this.arrOrders = arrOrders;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterSellersOrdersItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_orders_list,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSellersOrdersItem.ItemViewHolder holder, int position) {

        Orders orders = arrOrders.get(position);

        String productId = orders.getProductId();
        String orderId = orders.getOrderId();
        String storeId = orders.getStoreId();
        String fishImageName = orders.getImageName();
        String productName = orders.getFishName();
        int productQuantity = orders.getQuantity();
        double productPrice = orders.getPricePerKilo();

        int imageResource = context.getResources().getIdentifier(fishImageName, "drawable", context.getPackageName());

        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_productPhoto);

        holder.tv_productName.setText(productName);
        holder.tv_productQty.setText(productQuantity + " Kg");
        holder.tv_productPrice.setText("₱ " + productPrice + " / Kg");

        holder.tv_viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, seller_order_details_page.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("storeId", storeId);
                intent.putExtra("productId", productId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrOrders.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        ImageView iv_productPhoto;
        TextView tv_productName, tv_productQty, tv_productPrice, tv_viewOrderBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_viewOrderBtn = itemView.findViewById(R.id.tv_viewOrderBtn);
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
