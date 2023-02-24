package Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Products;

public class AdapterStoreProductsItem extends RecyclerView.Adapter<AdapterStoreProductsItem.ItemViewHolder> {

    private List<Products> arrProducts = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterStoreProductsItem() {
    }

    public AdapterStoreProductsItem(List<Products> arrProducts, Context context) {
        this.arrProducts = arrProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterStoreProductsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_product,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreProductsItem.ItemViewHolder holder, int position) {

        Products products = arrProducts.get(position);

        String fishImageName = products.getImageName();
        String productName = products.getFishName();
        int productQuantity = products.getQuantityByKilo();
        double productPrice = products.getPricePerKilo();


        int imageResource = context.getResources().getIdentifier(fishImageName, "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, imageResource);



        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_productPhoto);

        holder.tv_productName.setText(productName);
        holder.tv_productQuantity.setText(productQuantity + " kilogram/s remaining.");
        holder.tv_productPrice.setText("₱ " + productPrice + " / Kg");

    }

    @Override
    public int getItemCount() {
        return arrProducts.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_productName, tv_productQuantity, tv_productPrice;
        ImageView iv_productPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);

            tv_productQuantity = itemView.findViewById(R.id.tv_productQuantity);
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
