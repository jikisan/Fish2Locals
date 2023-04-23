package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterTransactionsItem;
import Models.Transactions;
import Models.Wallets;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class view_my_wallet_page extends AppCompatActivity {

    private List<Transactions> arrTransactions = new ArrayList<>();

    private TextView tv_fundBalance, tv_back, tv_withdrawBalanceBtn, tv_message;
    private ImageView iv_addFund;
    private RecyclerView recyclerView;
    private AdapterTransactionsItem adapterTransactionsItem;

    private DatabaseReference walletDatabase, transactionDatabase;
    private FirebaseUser user;

    private List<Wallets> arrWallets = new ArrayList<Wallets>();

    private String myUserId, walletId, timeCreated, dateCreated, myFundAmountString;
    private double myFundAmount;
    private long dateTimeInMillis;
    private ProgressDialog progressDialog;
    private SweetAlertDialog sDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_wallet_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        setRef(); // initialize UI Id's
        generateWalletData(); // generate wallet data from database
        generateRecyclerLayout(); // generate recyclerview layout
        clicks(); // buttons
    }

    private void generateRecyclerLayout() {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view_my_wallet_page.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterTransactionsItem = new AdapterTransactionsItem(arrTransactions);
        recyclerView.setAdapter(adapterTransactionsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = transactionDatabase.orderByChild("ownerID").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrTransactions.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Transactions transactions = dataSnapshot.getValue(Transactions.class);

                        arrTransactions.add(transactions);
                    }
                }

                if(arrTransactions.isEmpty())
                {
                    tv_message.setVisibility(View.VISIBLE);
                }

                Collections.reverse(arrTransactions);

                adapterTransactionsItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateWalletData() {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        walletDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);
                        String walletUserId = wallets.getUserID();

                        if(walletUserId.equals(myUserId))
                        {
                            walletId = dataSnapshot.getKey().toString();
                            myFundAmount = wallets.getFundAmount();
                            myFundAmountString = NumberFormat.getNumberInstance(Locale.US).format(myFundAmount);


                            tv_fundBalance.setText(myFundAmountString);
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

//            Intent intent = new Intent(view_my_wallet_page.this, homepage.class);
//            intent.putExtra("pageNumber", "5");
//            startActivity(intent);
            }
        });

        iv_addFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_my_wallet_page.this, add_funds_page.class);
                startActivity(intent);
            }
        });

        tv_withdrawBalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sDialog = new SweetAlertDialog(view_my_wallet_page.this, SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Withdraw Funds");
                sDialog.setCancelText("Back");
                sDialog.setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        progressDialog = new ProgressDialog(view_my_wallet_page.this);
                        progressDialog.setTitle("Processing review...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        deductFunds();

                    }
                });
                sDialog.setContentText("Withdraw Php " + myFundAmountString + "?");
                sDialog.show();



            }
        });
    }

    private void deductFunds() {

        if(myFundAmount <= 0 ){
            Toast.makeText(view_my_wallet_page.this, "Withdrawal failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateWalletData();

        }
    }

    private void updateWalletData() {

        double newFundValue = myFundAmount - myFundAmount;

        String transFundInString = tv_fundBalance.getText().toString();
        double transFundInDouble = Double.parseDouble(transFundInString.replace(",", ""));

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", myUserId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateTransactionData(newFundValue, transFundInDouble);
            }
        });
    }

    private void updateTransactionData(double newFundValue, double transFundInDouble) {

        setUpDate();

        String transactionType = "deduct";
        String transactionNote = "Fund Withdrawal";



        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, transFundInDouble, myUserId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(view_my_wallet_page.this, "Withdrawal Success!", Toast.LENGTH_SHORT).show();

                String myFundAmountString = NumberFormat.getNumberInstance(Locale.US).format(newFundValue);

                tv_fundBalance.setText(myFundAmountString);
                adapterTransactionsItem.notifyDataSetChanged();
                sDialog.dismiss();
                progressDialog.dismiss();

            }
        });
    }

    private void setRef() {

        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        tv_back = findViewById(R.id.tv_back);
        tv_withdrawBalanceBtn = findViewById(R.id.tv_withdrawBalanceBtn);
        tv_message = findViewById(R.id.tv_message);

        recyclerView = findViewById(R.id.recyclerView);

        iv_addFund = findViewById(R.id.iv_addFund);
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
}