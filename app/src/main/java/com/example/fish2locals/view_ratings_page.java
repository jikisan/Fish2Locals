package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class view_ratings_page extends AppCompatActivity {

    private RecyclerView recyclerView_reviews;
    private TextView tv_noReviews, tv_back;


    private DatabaseReference ratingDatabase;

    private String ratingOfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ratings_page);

        setRef();
        clicks();
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setRef() {


        tv_noReviews = findViewById(R.id.tv_noReviews);
        tv_back = findViewById(R.id.tv_back);

    }
}