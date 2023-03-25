package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import Models.Transactions;
import Models.Wallets;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class add_funds_page extends AppCompatActivity {

    private LinearLayout linearLayout;
    private TextView tv_back, tv_fundBalance, tv_addFundBtn;
    private ImageView iv_proofOfPayment;
    private EditText et_inputFund;

    private DatabaseReference walletDatabase, userDatabase, fundRequestDatabase, transactionDatabase;
    private StorageReference fundReqStorage;
    private FirebaseUser user;

    private String myUserID, category, walletId, timeCreated, dateCreated, myFundAmountString;
    private double fundAmount;
    private Uri imageUri;
    private long dateTimeInMillis;

    private SweetAlertDialog pDialog;

    private static final int PIC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_funds_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();

        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        fundRequestDatabase = FirebaseDatabase.getInstance().getReference("Fund Requests");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        fundReqStorage = FirebaseStorage.getInstance().getReference("Fund Requests").child(myUserID);


        setRef();
        generateWalletData();
        clicks();
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(add_funds_page.this, view_my_wallet_page.class);
//                startActivity(intent);

                onBackPressed();

            }
        });

        tv_addFundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String addFundInString = et_inputFund.getText().toString();
                double addFundInDouble = Double.parseDouble(addFundInString);

                if(TextUtils.isEmpty(addFundInString))
                    Toast.makeText(add_funds_page.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                else if(imageUri == null){
                    Toast.makeText(add_funds_page.this, "Proof of payment is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    double enteredFundAmount = Double.parseDouble(et_inputFund.getText().toString());

                    pDialog = new SweetAlertDialog(add_funds_page.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText("ADDING FUNDS");
                    pDialog.setContentText("Add PHP " + enteredFundAmount + " \nto your wallet?");
                    pDialog.setCancelText("Back");
                    pDialog.setConfirmButton("Add", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {


                                    final ProgressDialog progressDialog = new ProgressDialog(add_funds_page.this);
                                    progressDialog.setTitle("Processing...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    pDialog.dismiss();

                                    addFunds(addFundInDouble, progressDialog);

                                }
                            })
                            .show();
                }
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage();
            }
        });
    }

    private void addFunds(double addFundInDouble, ProgressDialog progressDialog) {

        double newFundValue = addFundInDouble + fundAmount;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", myUserID);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    updateTransactionData(addFundInDouble, progressDialog);

                }

            }
        });
    }

    private void updateTransactionData(double addFundInDouble, ProgressDialog progressDialog) {

        setUpDate();

        String transactionType = "add";
        String transactionNote = "Fund top-up";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, addFundInDouble, myUserID);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(add_funds_page.this, "PHP " + addFundInDouble +
                        " amount" +  " is successfully added.", Toast.LENGTH_SHORT).show();
                generateWalletData();
                imageUri = null;
                iv_proofOfPayment.setImageDrawable(null);
                et_inputFund.setText("");
                et_inputFund.setText(null);

                progressDialog.dismiss();

            }
        });

    }

    private void generateWalletData() {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        walletId = dataSnapshot.getKey().toString();
                        fundAmount = wallets.getFundAmount();
                        myFundAmountString = NumberFormat.getNumberInstance(Locale.US).format(fundAmount);

                        tv_fundBalance.setText(myFundAmountString);
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void PickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PIC);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

            Picasso.get().load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(iv_proofOfPayment);
        }
    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }

    private void setRef() {

        linearLayout = findViewById(R.id.linearLayout);

        tv_back = findViewById(R.id.tv_back);
        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        tv_addFundBtn = findViewById(R.id.tv_addFundBtn);

        iv_proofOfPayment = findViewById(R.id.iv_proofOfPayment);

        et_inputFund = findViewById(R.id.et_inputFund);


    }
}