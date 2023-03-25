package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fish2locals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Models.Ratings;
import Models.Users;
import Objects.TextModifier;

public class AdapterReviewsItem extends RecyclerView.Adapter<AdapterReviewsItem.ItemViewHolder> {

    private List<Ratings> arrRatings;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public AdapterReviewsItem() {
    }

    public AdapterReviewsItem(List<Ratings> arrRatings, Context context) {
        this.arrRatings = arrRatings;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratings,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Ratings ratings = arrRatings.get(position);

        String ratedById = ratings.getRatedById();

        generateRaterName(ratedById, holder);

        double ratingsValue = ratings.getRatingValue();

        String message = ratings.getRatingMessage();

        holder.rb_userRating.setRating((float)ratingsValue);

        holder.tv_message.setText(message);

    }

    private void generateRaterName(String ratedById, ItemViewHolder holder) {

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        userDatabase.child(ratedById).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    TextModifier textModifier = new TextModifier();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();

                    String userName = fname +" "+ lname;
                    holder.tv_userName.setText("by: " + userName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrRatings.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_userName, tv_message;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_message = itemView.findViewById(R.id.tv_message);

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
