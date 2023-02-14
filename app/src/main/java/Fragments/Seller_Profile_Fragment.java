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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Seller_Profile_Fragment extends Fragment {

    private Switch sw_switchBtn;

    private FirebaseUser user;
    private DatabaseReference userDatabase;

    private String myUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller__profile_, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef(view);
        clicks();

        return view;
    }

    private void clicks() {

        sw_switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sw_switchBtn.setChecked(false);

                boolean sellerMode = false;

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("sellerMode", sellerMode);

                userDatabase.child(myUserId).updateChildren(hashMap);

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