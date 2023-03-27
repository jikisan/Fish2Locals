package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Photos;

public class AdapterPhotoItem extends RecyclerView.Adapter<AdapterPhotoItem.ItemViewHolder> {

    List<Photos> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterPhotoItem() {
    }

    public AdapterPhotoItem(List<Photos> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Photos photos = arr.get(position);

        String imageUrl = photos.getLink();

        Picasso.get()
                .load(imageUrl)
                .into(holder.iv_photoItem);


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
        ImageView iv_photoItem;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_photoItem = itemView.findViewById(R.id.iv_photoItem);

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
