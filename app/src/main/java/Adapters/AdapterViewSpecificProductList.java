package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.example.fish2locals.view_store_page;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

import Models.TempBestSellerData;
import Models.TempStoreData;

public class AdapterViewSpecificProductList extends RecyclerView.Adapter<AdapterViewSpecificProductList.ItemViewHolder> {

    private List<TempBestSellerData> arrTempBestSeller;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public AdapterViewSpecificProductList() {
    }

    public AdapterViewSpecificProductList(List<TempBestSellerData> arrTempBestSeller, Context context) {
        this.arrTempBestSeller = arrTempBestSeller;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewSpecificProductList.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fish_lists,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewSpecificProductList.ItemViewHolder holder, int position) {

        TempBestSellerData tempBestSellerData = arrTempBestSeller.get(position);


        String productImageName = tempBestSellerData.getImageName();
        String fishName = tempBestSellerData.getFishName();
        double price = tempBestSellerData.getPricePerKilo();
        int quantity = tempBestSellerData.getQuantityByKilo();
        String storeName = tempBestSellerData.getStoreName();
        double distance = tempBestSellerData.getDistance();
        double averageRating = tempBestSellerData.getRatings();
        String storeId = tempBestSellerData.getStoreId();
        String storeOwnersUserId = tempBestSellerData.getStoreOwnersUserId();

        DecimalFormat df = new DecimalFormat("#.00");
        df.format(distance);

        if (distance > 1000) {

            double kilometers = distance / 1000;
            holder.tv_distance.setText(df.format(kilometers) + " Km Away");

        } else {

            holder.tv_distance.setText(df.format(distance) + " m Away");

        }

        int imageResource = context.getResources().getIdentifier(productImageName, "drawable", context.getPackageName());

        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_productPhoto);

        holder.tv_fishName.setText(fishName);
        holder.tv_productQuantity.setText(quantity + " kilogram/s remaining.");
        holder.tv_productPrice.setText("₱ " + price + " / Kg");

        holder.tv_storeName.setText("Sold by: " + storeName);
        holder.tv_userRatingCount.setText(averageRating + "");
        holder.rb_userRating.setRating((float) averageRating);

        holder.tv_visitStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, view_store_page.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                view.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrTempBestSeller.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_productPhoto;
        TextView tv_fishName, tv_productQuantity, tv_productPrice;
        TextView tv_storeName, tv_distance, tv_userRatingCount, tv_visitStoreBtn;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_productPhoto = itemView.findViewById(R.id.iv_productPhoto);

            tv_fishName = itemView.findViewById(R.id.tv_fishName);
            tv_productQuantity = itemView.findViewById(R.id.tv_productQuantity);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);

            tv_storeName = itemView.findViewById(R.id.tv_storeName);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_userRatingCount = itemView.findViewById(R.id.tv_userRatingCount);
            tv_visitStoreBtn = itemView.findViewById(R.id.tv_visitStoreBtn);

            rb_userRating = itemView.findViewById(R.id.rb_userRating);

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
