package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.example.fish2locals.view_store_page;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import Models.Ratings;
import Models.Store;
import Models.TempStoreData;

public class AdapterStoresNearMe extends RecyclerView.Adapter<AdapterStoresNearMe.ItemViewHolder> {

    private List<Store> arrStore;
    private List<TempStoreData> arrTempStoreData;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public AdapterStoresNearMe() {
    }

    public AdapterStoresNearMe(List<Store> arrStore, List<TempStoreData> arrTempStoreData, Context context) {
        this.arrStore = arrStore;
        this.arrTempStoreData = arrTempStoreData;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterStoresNearMe.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stores,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoresNearMe.ItemViewHolder holder, int position) {

        TempStoreData tempStoreData = arrTempStoreData.get(position);

        String storeUrl = tempStoreData.getStoreUrl();
        String storeName = tempStoreData.getStoreName();
        double distance = tempStoreData.getDistance();
        double averageRating = tempStoreData.getRatings();
        int count = tempStoreData.getRatingsCount();
        String storeId = tempStoreData.getStoreId();
        String storeOwnersUserId = tempStoreData.getStoreOwnersUserId();

        DecimalFormat df = new DecimalFormat("#.00");
        df.format(distance);
        if (distance > 1000) {
            double kilometers = distance / 1000;
            holder.tv_distance.setText(df.format(kilometers) + " Km Away");
        } else {
            holder.tv_distance.setText(df.format(distance) + " m Away");
        }

        Picasso.get()
                .load(storeUrl)
                .fit()
                .centerCrop()
                .into(holder.iv_storePhoto);

        holder.tv_storeName.setText(storeName);

        holder.tv_userRatingCount.setText(averageRating + "");
        holder.rb_userRating.setRating((float) averageRating);

        holder.tv_visitStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, view_store_page.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("ratingsId", storeId);
                view.getContext().startActivity(intent);
            }
        });


    }

    private void generateStoreReview(ItemViewHolder holder, String storeId) {

        DatabaseReference ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(storeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    String ratingCounter = "(" + String.valueOf(counter) + ")";

                    holder.tv_userRatingCount.setText(ratingCounter);
                    holder.rb_userRating.setRating((float) averageRating);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {

        if(arrTempStoreData.size() < 10)
        {
            return arrTempStoreData.size();
        }
        else
            return 10;

    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_storePhoto;
        TextView tv_storeName, tv_distance, tv_userRatingCount, tv_visitStoreBtn;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_storePhoto = itemView.findViewById(R.id.iv_storePhoto);

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
