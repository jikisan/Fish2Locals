package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;

import org.w3c.dom.Text;

import java.util.List;

import Models.Notifications;
import Models.Orders;

public class AdapterPendingInTransit extends RecyclerView.Adapter<AdapterPendingInTransit.ItemViewHolder> {

    private List<Orders> arrOrders;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public AdapterPendingInTransit() {
    }

    public AdapterPendingInTransit(List<Orders> arrOrders, Context context) {
        this.arrOrders = arrOrders;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPendingInTransit.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_transit_lists,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPendingInTransit.ItemViewHolder holder, int position) {

        Orders orders = arrOrders.get(position);

        String dateCreated = orders.getDateCreated();
        String timeCreated = orders.getTimeCreated();
        double price = orders.getPricePerKilo();
        int quantity = orders.getQuantity();

        String dateTimeCreated = dateCreated + " " + timeCreated;
        double totatPrice = quantity * price;

        holder.tv_date.setText(dateTimeCreated);
        holder.tv_price.setText("₱ " + totatPrice);

    }

    @Override
    public int getItemCount() {
        return arrOrders.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date, tv_price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }
}
