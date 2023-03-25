package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fish2locals.R;
import com.example.fish2locals.view_ratings_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterReviewsItem;
import Models.Ratings;


public class StoreReviewsFragment extends Fragment {

    private List<Ratings> arrRatings = new ArrayList<>();
    private AdapterReviewsItem adapterReviewsItem;

    private RecyclerView rv_storeReviews;
    private TextView tv_noReviews, tv_back;
    private ProgressBar progressBar;

    private FirebaseUser user;
    private DatabaseReference ratingDatabase;

    private String myUserId, ratingsId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_reviews, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        ratingsId = getActivity().getIntent().getStringExtra("ratingsId");

        setRef(view);
        generateRecyclerLayout();
        return view;
    }

    private void generateRecyclerLayout() {

        rv_storeReviews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_storeReviews.setLayoutManager(linearLayoutManager);

        adapterReviewsItem = new AdapterReviewsItem(arrRatings, getContext());
        rv_storeReviews.setAdapter(adapterReviewsItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(ratingsId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);

                        arrRatings.add(ratings);
                    }

                }

                if(arrRatings.isEmpty())
                {
                    tv_noReviews.setVisibility(View.VISIBLE);
                    rv_storeReviews.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                adapterReviewsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        tv_noReviews = view.findViewById(R.id.tv_noReviews);
        rv_storeReviews = view.findViewById(R.id.rv_storeReviews);
        progressBar = view.findViewById(R.id.progressBar);
    }
}