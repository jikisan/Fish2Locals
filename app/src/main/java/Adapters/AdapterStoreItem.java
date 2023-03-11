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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Store;
import Models.TempStoreData;

public class AdapterStoreItem extends RecyclerView.Adapter<AdapterStoreItem.ItemViewHolder> {

    private List<TempStoreData> arrStore = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterStoreItem() {
    }

    public AdapterStoreItem(List<TempStoreData> arrStore, Context context) {
        this.arrStore = arrStore;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterStoreItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_search_results, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreItem.ItemViewHolder holder, int position) {

        TempStoreData store = arrStore.get(position);

        String storeUrl = store.getStoreUrl();
        String storeName = store.getStoreName();

        Picasso.get()
                .load(storeUrl)
                .fit()
                .centerCrop()
                .into(holder.iv_storePhoto);

        holder.tv_storeName.setText(storeName);

    }

    @Override
    public int getItemCount() {
        return arrStore.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_storePhoto;
        private TextView tv_storeName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_storePhoto = itemView.findViewById(R.id.iv_storePhoto);
            tv_storeName = itemView.findViewById(R.id.tv_storeName);

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
