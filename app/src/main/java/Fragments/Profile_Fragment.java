package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.example.fish2locals.R;
import com.example.fish2locals.homepage;
import com.example.fish2locals.seller_application;
import com.example.fish2locals.seller_homepage;

public class Profile_Fragment extends Fragment {

    private Button btn_applyAsSeller;
    private Switch sw_switchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setRef(view);
        clicks();

        return view;
    }

    private void clicks() {

        btn_applyAsSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), seller_application.class);
                startActivity(intent);
            }
        });

        sw_switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });

    }

    private void setRef(View view) {

        btn_applyAsSeller = view.findViewById(R.id.btn_applyAsSeller);
        sw_switchBtn = view.findViewById(R.id.sw_switchBtn);

    }
}