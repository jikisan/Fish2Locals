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
import com.example.fish2locals.view_stores_of_best_sellers_page;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterMarketPlace extends RecyclerView.Adapter<AdapterMarketPlace.ItemViewHolder> {

    private String[] fish;
    private int images[];



    private String fishName;
    private int fishImageName;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterMarketPlace() {
    }

    public AdapterMarketPlace(String[] fish, int[] images, Context context) {
        this.fish = fish;
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMarketPlace.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMarketPlace.ItemViewHolder holder, int position) {

        String fishName = fish[position];
        int fishImageName = images[position];
        String imageName = context.getResources().getResourceEntryName(fishImageName);

        Picasso.get()
                .load(fishImageName)
                .fit()
                .centerCrop()
                .into(holder.iv_fishPhoto);


        holder.tv_fishName.setText(fishName);

        holder.tv_viewStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, view_stores_of_best_sellers_page.class);
                intent.putExtra("fishImageName", imageName);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fish.length;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_fishPhoto;
        TextView tv_fishName, tv_viewStoreBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_fishPhoto = itemView.findViewById(R.id.iv_fishPhoto);

            tv_fishName = itemView.findViewById(R.id.tv_fishName);
            tv_viewStoreBtn = itemView.findViewById(R.id.tv_viewStoreBtn);


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
