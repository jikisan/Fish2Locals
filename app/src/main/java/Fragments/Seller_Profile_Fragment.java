package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.fish2locals.R;
import com.example.fish2locals.homepage;

public class Seller_Profile_Fragment extends Fragment {

    private Switch sw_switchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller__profile_, container, false);

        setRef(view);
        clicks();

        return view;
    }

    private void clicks() {

        sw_switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sw_switchBtn.setChecked(false);

                Intent intent = new Intent(getContext(), homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);

            }
        });
    }

    private void setRef(View view) {

        sw_switchBtn = view.findViewById(R.id.sw_switchBtn);

    }
}