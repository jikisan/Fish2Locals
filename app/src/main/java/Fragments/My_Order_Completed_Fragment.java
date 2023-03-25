package Fragments;

import static android.view.View.GONE;

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

import Adapters.AdapterMyOrdersItem;
import Models.Orders;

public class My_Order_Completed_Fragment extends Fragment {

    private List<Orders> arrOrders = new ArrayList<>();
    private List<String> arrProductIds = new ArrayList<>();
    private AdapterMyOrdersItem adapterMyOrdersItem;

    private ProgressBar progressBar;
    private RecyclerView rv_myOrders;
    private TextView tv_back, tv_textPlaceholder;

    private FirebaseUser user;
    private DatabaseReference ordersDatabase;

    private String myUserId, storeOwnersUserId, storeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my__order__completed_, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        storeOwnersUserId = getActivity().getIntent().getStringExtra("storeOwnersUserId");
        storeId = getActivity().getIntent().getStringExtra("storeId");

        setRef(view);
        generateRecyclerLayout();
        return view;
    }

    private void generateRecyclerLayout() {

        rv_myOrders.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_myOrders.setLayoutManager(linearLayoutManager);

        adapterMyOrdersItem = new AdapterMyOrdersItem(arrOrders, getContext());
        rv_myOrders.setAdapter(adapterMyOrdersItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = ordersDatabase
                .orderByChild("buyerUserId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrOrders.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);

                        String orderStatus = orders.getOrderStatus();

                        if(orderStatus.equals("2"))
                        {
                            arrOrders.add(orders);
                        }

                        //                        String imageName = orders.getImageName();
                        //                        String fishName = orders.getFishName();
                        //                        double pricePerKilo = orders.getPricePerKilo();
                        //                        boolean pickup  = orders.isPickup();
                        //                        boolean ownDelivery = orders.isOwnDelivery();
                        //                        boolean thirrdPartyDelivery = orders.isThirdPartyDelivery();
                        //                        int quantityByKilo = orders.getQuantity();
                        //                        String storeId = orders.getStoreId();
                        //                        String sellerUserId = orders.getSellerUserId();
                        //                        String buyerUserId = orders.getBuyerUserId();
                        //
                        //                        Basket basket = new Basket(imageName, fishName, pricePerKilo, pickup,
                        //                                ownDelivery, thirrdPartyDelivery, quantityByKilo, storeId,
                        //                                sellerUserId, buyerUserId);

                    }
                }


                progressBar.setVisibility(GONE);

                if(arrOrders.isEmpty())
                {
                    tv_textPlaceholder.setVisibility(View.VISIBLE);
                    tv_textPlaceholder.setText("Empty");
                }

                adapterMyOrdersItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        rv_myOrders = view.findViewById(R.id.rv_myOrders);

        tv_back = view.findViewById(R.id.tv_back);
        tv_textPlaceholder = view.findViewById(R.id.tv_textPlaceholder);

    }
}