package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.fish2locals.R;
import com.example.fish2locals.homepage;
import com.example.fish2locals.seller_application;
import com.example.fish2locals.seller_homepage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import Models.Users;

public class Profile_Fragment extends Fragment {

    private Button btn_applyAsSeller;
    private Switch sw_switchBtn;
    private LinearLayout layout_noSeller, layout_hasSeller;

    private FirebaseUser user;
    private DatabaseReference userDatabase;

    private String myUserId;
    private boolean hasSellerAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");


        setRef(view);
        checkIfHasSellerAccount();
        clicks();

        return view;
    }

    private void checkIfHasSellerAccount() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    hasSellerAccount = users.isHasSellerAccount();

                    if(hasSellerAccount == true)
                    {
                        layout_hasSeller.setVisibility(View.VISIBLE);
                        layout_noSeller.setVisibility(View.GONE);
                    }
                    else if (hasSellerAccount == false)
                    {
                        layout_hasSeller.setVisibility(View.GONE);
                        layout_noSeller.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

                sw_switchBtn.setChecked(true);

                boolean sellerMode = true;

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("sellerMode", sellerMode);

                userDatabase.child(myUserId).updateChildren(hashMap);

                Intent intent = new Intent(getContext(), seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });

    }

    private void setRef(View view) {

        btn_applyAsSeller = view.findViewById(R.id.btn_applyAsSeller);
        sw_switchBtn = view.findViewById(R.id.sw_switchBtn);

        layout_hasSeller = view.findViewById(R.id.layout_hasSeller);
        layout_noSeller = view.findViewById(R.id.layout_noSeller);

    }
}