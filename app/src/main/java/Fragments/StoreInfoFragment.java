package Fragments;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fish2locals.R;
import com.example.fish2locals.view_store_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import Adapters.AdapterStoreProductsItem;
import Models.Bookmark;
import Models.Products;
import Models.Store;


public class StoreInfoFragment extends Fragment {

    private List<Store> arrStore = new ArrayList<>();

    private ProgressBar progressBar;
    private ImageView iv_bookmarkOn, iv_bookmarkCancel, iv_messageStoreBtn;
    private TextView tv_storeName, tv_storeContactPerson, tv_storeContactNum,
            tv_storeAddress;

    private FirebaseUser user;
    private DatabaseReference storeDatabase, bookmarkDatabase;

    private String myUserId, storeOwnersUserId, storeId, bookmarkId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_info, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference("Bookmark");

        storeOwnersUserId = getActivity().getIntent().getStringExtra("storeOwnersUserId");
        storeId = getActivity().getIntent().getStringExtra("storeId");

        setRef(view);
        checkIfBookmarked();
        generateStoreData();
        click();
        return view;
    }

    private void checkIfBookmarked() {

        Query query = bookmarkDatabase.orderByChild("userId").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Bookmark bookmark = dataSnapshot.getValue(Bookmark.class);

                        String storeIdBookmark = bookmark.getStoreId();
                        bookmarkId = dataSnapshot.getKey();

                        if(storeId.equals(storeIdBookmark))
                        {
                            iv_bookmarkOn.setVisibility(View.GONE);
                            iv_bookmarkCancel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            iv_bookmarkOn.setVisibility(View.VISIBLE);
                            iv_bookmarkCancel.setVisibility(GONE);
                        }
                    }

                }
                else
                {
                    iv_bookmarkOn.setVisibility(View.VISIBLE);
                    iv_bookmarkCancel.setVisibility(GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateStoreData() {

        storeDatabase.child(storeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Store store = snapshot.getValue(Store.class);

                    String storeName = store.getStoreName();
                    String storeContactPerson = store.getStoreContactPerson();
                    String contactNum = store.getStoreContactNum();
                    String storeAddress = store.getStoreAddress();

                    tv_storeName.setText(storeName);
                    tv_storeContactPerson.setText(storeContactPerson);
                    tv_storeContactNum.setText(contactNum);
                    tv_storeAddress.setText(storeAddress);

                    arrStore.add(store);


                }

                progressBar.setVisibility(GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void click() {

        iv_bookmarkOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String storeUrl = arrStore.get(0).getStoreUrl();
                String storeName = arrStore.get(0).getStoreName();
                String storeLat = arrStore.get(0).getStoreLat();
                String storeLang = arrStore.get(0).getStoreLang();
                long ratings = arrStore.get(0).getRatings();

                Bookmark bookmark = new Bookmark(storeUrl, storeName, storeLat, storeLang, ratings,
                        myUserId, storeId);

                bookmarkDatabase.push().setValue(bookmark).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            iv_bookmarkOn.setVisibility(View.GONE);
                            iv_bookmarkCancel.setVisibility(View.VISIBLE);

                            Toast.makeText(getContext(), "You've bookmarked this store.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        iv_bookmarkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = bookmarkDatabase.orderByChild("userId").equalTo(myUserId);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                Bookmark bookmark = dataSnapshot.getValue(Bookmark.class);

                                String storeIdBookmark = bookmark.getStoreId();
                                bookmarkId = dataSnapshot.getKey();


                                if(storeId.equals(storeIdBookmark))
                                {
                                    Toast.makeText(getContext(), "You've unbookmark this store.", Toast.LENGTH_SHORT).show();

                                    iv_bookmarkOn.setVisibility(View.VISIBLE);
                                    iv_bookmarkCancel.setVisibility(GONE);
                                    dataSnapshot.getRef().removeValue();

                                }
                            }

                        }
                        else {
                            iv_bookmarkOn.setVisibility(View.VISIBLE);
                            iv_bookmarkCancel.setVisibility(GONE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        iv_messageStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Message Button", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        iv_bookmarkOn = view.findViewById(R.id.iv_bookmarkOn);
        iv_bookmarkCancel = view.findViewById(R.id.iv_bookmarkCancel);
        iv_messageStoreBtn = view.findViewById(R.id.iv_messageStoreBtn);

        tv_storeName = view.findViewById(R.id.tv_storeName);
        tv_storeContactPerson = view.findViewById(R.id.tv_storeContactPerson);
        tv_storeContactNum = view.findViewById(R.id.tv_storeContactNum);
        tv_storeAddress = view.findViewById(R.id.tv_storeAddress);

    }
}