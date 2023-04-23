package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class edit_profile_page extends AppCompatActivity {

    private ImageView iv_userPhoto;
    private EditText et_firstName, et_lastName, et_contactNumber;
    private TextView tv_submitBtn, tv_uploadProfilePhotoBtn, tv_back;

    private FirebaseAuth fAuth;
    private DatabaseReference userDatabase;
    private StorageReference userStorage;
    private FirebaseUser user;

    public static final int USER_PIC = 3;

    private String myUserId;
    private Uri userPicUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userStorage = FirebaseStorage.getInstance().getReference("Users");

        generateMyData(); //access user data in database
        setRef();  // initialize UI ID's
        clicks(); // button
    }

    private void generateMyData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    String imageUrl = users.getImageUrl();
                    String fname = users.getFname();
                    String lname = users.getLname();
                    String contactNum = users.getContactNum();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_userPhoto);
                    }

                    et_firstName.setText(fname);
                    et_lastName.setText(lname);
                    et_contactNumber.setText(contactNum);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clicks() {

        tv_uploadProfilePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage();
            }
        });

        tv_submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();


            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void validate() {

        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String contactNum = et_contactNumber.getText().toString();

        if (TextUtils.isEmpty(firstName))
        {
            et_firstName.setError("This field is required");
        }
        else if (TextUtils.isEmpty(lastName))
        {
            et_lastName.setError("This field is required");
        }
        else if (TextUtils.isEmpty(contactNum))
        {
            et_contactNumber.setError("This field is required");
        }
        else if (contactNum.length() != 11)
        {
            et_contactNumber.setError("Contact number must be 11 digit");
        }
        else
        {
            new AlertDialog.Builder(edit_profile_page.this)
                    .setTitle("UPDATE PROFILE")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(true)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog = new ProgressDialog(edit_profile_page.this);
                            progressDialog.setTitle("Updating Profile");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            submit();

                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                        }
                    })
                    .show();

        }
    }

    private void submit() {

        if(userPicUri == null)
        {
            saveWithOutImage();

        }else
        {
            saveWithImage();
        }
    }

    private void PickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),USER_PIC);

    }

    private void saveWithImage() {

        String newImageName = userPicUri.getLastPathSegment();
        StorageReference userPicRef = userStorage.child(myUserId).child(newImageName);

        userPicRef.putFile(userPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                userPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String newImageUrl = uri.toString();
                        String newFname = et_firstName.getText().toString();
                        String newLname = et_lastName.getText().toString();
                        String newContactNum = et_contactNumber.getText().toString();

                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("imageUrl", newImageUrl);
                        hashMap.put("imageName", newImageName);
                        hashMap.put("fname", newFname);
                        hashMap.put("lname", newLname);
                        hashMap.put("contactNum", newContactNum);

                        userDatabase.child(myUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    SweetAlertDialog sDialog;
                                    sDialog =  new SweetAlertDialog(edit_profile_page.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sDialog.setTitleText("SUCCESS!");
                                    sDialog.setCancelable(false);
                                    sDialog.setContentText("Profile Updated!.");
                                    sDialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            progressDialog.dismiss();
                                            Intent intent = new Intent(edit_profile_page.this, homepage.class);
                                            intent.putExtra("pageNumber", "5");
                                            startActivity(intent);
                                        }
                                    });
                                    sDialog.show();
                                }

                            }
                        });

                    }
                });
            }
        });
    }

    private void saveWithOutImage() {

        String newFname = et_firstName.getText().toString();
        String newLname = et_lastName.getText().toString();
        String newContactNum = et_contactNumber.getText().toString();


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("fname", newFname);
        hashMap.put("lname", newLname);
        hashMap.put("contactNum", newContactNum);

        userDatabase.child(myUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    new SweetAlertDialog(edit_profile_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("SUCCESS!")
                            .setContentText("Profile Updated!.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    progressDialog.dismiss();
                                    Intent intent = new Intent(edit_profile_page.this, homepage.class);
                                    intent.putExtra("pageNumber", "5");
                                    startActivity(intent);
                                }
                            })
                            .show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==USER_PIC && resultCode == Activity.RESULT_OK)
        {
            userPicUri = data.getData();

            Picasso.get().load(userPicUri)
                    .into(iv_userPhoto);
        }
    }

    private void setRef() {

        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);

        tv_submitBtn = findViewById(R.id.tv_submitBtn);
        tv_uploadProfilePhotoBtn = findViewById(R.id.tv_uploadProfilePhotoBtn);
        tv_back = findViewById(R.id.tv_back);

        fAuth = FirebaseAuth.getInstance();
    }

}