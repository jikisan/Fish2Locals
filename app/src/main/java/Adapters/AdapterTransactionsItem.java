package Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;

import java.util.List;

import Models.Transactions;

public class AdapterTransactionsItem extends RecyclerView.Adapter<AdapterTransactionsItem.ItemViewHolder> {

    private List<Transactions> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterTransactionsItem() {
    }

    public AdapterTransactionsItem(List<Transactions> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Transactions transactions = arr.get(position);


        String transactionType = transactions.getTransactionType();
        String transactionNote = transactions.getTransactionNote();
        double transactionAmount = transactions.getTransactionAmount();

        String transactionDate = transactions.getTransactionDate();
        String transactionTime = transactions.getTransactionTime();
        String dateTime = transactionDate + " " + transactionTime;

        holder.tv_transNote.setText(transactionNote);
        holder.tv_transDateTime.setText(dateTime);

        if(transactionType.equals("add"))
        {
            holder.tv_transAmount.setTextColor(Color.parseColor("#00FF00"));
            holder.tv_transAmount.setText("+" + transactionAmount);
        }
        else if(transactionType.equals("deduct"))
        {
            holder.tv_transAmount.setTextColor(Color.parseColor("#FF0000"));
            holder.tv_transAmount.setText("-" + transactionAmount);
        }

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

        TextView tv_transNote, tv_transAmount, tv_transDateTime;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_transNote = itemView.findViewById(R.id.tv_transNote);
            tv_transAmount = itemView.findViewById(R.id.tv_transAmount);
            tv_transDateTime = itemView.findViewById(R.id.tv_transDateTime);

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
