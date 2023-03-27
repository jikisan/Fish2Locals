package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.AdapterViewPagerPhotoFullscreen;
import Models.Photos;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class view_photo_fullscreen_page extends AppCompatActivity {

    private AdapterViewPagerPhotoFullscreen adapterViewPagerPhotoFullscreen;
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private String productId, category, imageName;
    private int currentPosition;
    private DatabaseReference photoDatabase;

    private ImageView iv_deletPhoto;
    private TextView tv_back;
    private ViewPager vp_photoFullscreen;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_fullscreen_page);

        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        productId = getIntent().getStringExtra("productId");
        currentPosition = getIntent().getIntExtra("current position", 0);
        category = getIntent().getStringExtra("category");

        setRef();

        if(category.equals("buyer"))
        {
            iv_deletPhoto.setVisibility(View.GONE);
        }

        getViewHolderValues();
        generateImageData();
        clicks();
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_deletPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference photoDB = FirebaseDatabase.getInstance().getReference("Photos");
                StorageReference photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(productId);

                deletePhotoInDb(photoDB, photoStorage, imageName, productId);
//                SweetAlertDialog sDialog = new SweetAlertDialog(view_photo_fullscreen_page.this, SweetAlertDialog.WARNING_TYPE);
//                sDialog.setTitleText("Warning!.");
//                sDialog.setContentText("Delete " + imageName + "?");
//                sDialog.setCancelText("Cancel");
//                sDialog.setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                                sDialog.dismiss();
//
//
//                            }
//                        });
//                sDialog.show();
            }
        });
    }

    private void deletePhotoInDb(DatabaseReference photoDB, StorageReference photoStorage, String imageName, String projID) {

        progressDialog = new ProgressDialog(view_photo_fullscreen_page.this);
        progressDialog.setTitle("Deleting photo: " + imageName );
        progressDialog.show();

        Query query = photoDB
                .orderByChild("photoName")
                .equalTo(imageName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = photoStorage.child(imageName);

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    imageRef.delete();
                    dataSnapshot.getRef().removeValue();

                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if(category.equals("add"))
                        {

                            progressDialog.dismiss();

                            Intent intent = new Intent(view_photo_fullscreen_page.this,
                                    add_photos_page.class);
                            intent.putExtra("productId", productId);
                            startActivity(intent);

                        }
                        else if(category.equals("viewer"))
                        {
//                            Toast.makeText(view_photo_fullscreen_page.this, "Photo: " + imageName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(view_photo_fullscreen_page.this, photo_viewer.class);
//                            intent.putExtra("Project ID", projID);
//                            view_photo_fullscreen_page.this.startActivity(intent);
                        }

                    }
                }, 4000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateImageData() {

        // Initializing the ViewPagerAdapter
        adapterViewPagerPhotoFullscreen = new AdapterViewPagerPhotoFullscreen(view_photo_fullscreen_page.this, arrUrl, currentPosition, category);

        // Adding the Adapter to the ViewPager
        vp_photoFullscreen.setAdapter(adapterViewPagerPhotoFullscreen);

        vp_photoFullscreen.postDelayed(new Runnable() {

            @Override
            public void run() {
                adapterViewPagerPhotoFullscreen.notifyDataSetChanged();
                vp_photoFullscreen.setCurrentItem(currentPosition);
            }
        }, 500);
//        vp_photoFullscreen.setCurrentItem(currentPosition);

        getViewHolderValues();

    }

    private void getViewHolderValues() {
        Query query = photoDatabase
                .orderByChild("productId")
                .equalTo(productId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrUrl.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Photos photos = dataSnapshot.getValue(Photos.class);
                    imageName = photos.getPhotoName().toString();
                    arrUrl.add(photos);
                }

                adapterViewPagerPhotoFullscreen.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        vp_photoFullscreen = findViewById(R.id.vp_photoFullscreen);
        tv_back = findViewById(R.id.tv_back);
        iv_deletPhoto = findViewById(R.id.iv_deletPhoto);

    }
}