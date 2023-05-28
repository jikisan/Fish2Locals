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
import com.example.fish2locals.view_stores_of_best_sellers_page;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Store;
import Models.TempStoreData;

public class AdapterBestSellers extends RecyclerView.Adapter<AdapterBestSellers.ItemViewHolder> {

    private List<String> arrProductNames = new ArrayList<>();
    private List<String> arrProductImageNames = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterBestSellers() {
    }

    public AdapterBestSellers(List<String> arrProductNames, List<String> arrProductImageNames, Context context) {
        this.arrProductNames = arrProductNames;
        this.arrProductImageNames = arrProductImageNames;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterBestSellers.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBestSellers.ItemViewHolder holder, int position) {

        String fishName = arrProductNames.get(position);
        String fishImageName = arrProductImageNames.get(position);

        int imageResource = context.getResources().getIdentifier(fishImageName, "drawable", context.getPackageName());

        Picasso.get()
                .load(imageResource)
                .fit()
                .centerCrop()
                .into(holder.iv_fishPhoto);

        String split[] = fishImageName.split("_");

        if(split.length >= 3 )
        {
            holder.tv_fishName.setText(split[1] +" / "+ split[2]);
        }
        else
        {
            holder.tv_fishName.setText(split[1]);
        }


        holder.tv_viewStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, view_stores_of_best_sellers_page.class);
                intent.putExtra("fishImageName", fishImageName);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        if(arrProductImageNames.size() > 6)
        {
            return 5;
        }
        else
        {
            return arrProductImageNames.size();
        }

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
