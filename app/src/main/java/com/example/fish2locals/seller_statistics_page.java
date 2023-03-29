package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Fragments.My_Order_Cancelled_Fragment;
import Fragments.My_Order_Completed_Fragment;
import Models.Orders;
import Models.Products;

public class seller_statistics_page extends AppCompatActivity {

    private TextView tv_back, tv_totalSales, tv_totalOrdersCount, tv_totalProductsCount,
        tv_inTransitCount, tv_completedCount, tv_cancelledCount;
    private BarChart barChart;

    private final ArrayList<BarEntry> entries = new ArrayList<>();
    private final String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec",};

    private DatabaseReference orderDatabase, productDatabase;

    private String myUserId;

    private int janValue = 0;
    private int febValue = 0;
    private int marValue = 0;
    private int aprValue = 0;
    private int mayValue = 0;
    private int junValue = 0;
    private int julValue = 0;
    private int augValue = 0;
    private int sepValue = 0;
    private int octValue = 0;
    private int novValue = 0;
    private int decValue = 0;

    ArrayList<String> labels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_statistics_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");


        setRef();
        generateTotalSales();
        generateOrdersData();
        generateProductsData();

        clicks();

    }


    private void generateTotalSales() {

        Query query = orderDatabase.orderByChild("sellerUserId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    double totalPricePerKilo = 0;

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);
                        double pricePerKilo = orders.getPricePerKilo();

                        totalPricePerKilo = totalPricePerKilo + pricePerKilo;

                    }

                    String totalSaleInString = NumberFormat.getNumberInstance(Locale.US).format(totalPricePerKilo);

                    tv_totalSales.setText(totalSaleInString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void generateOrdersData() {

        Query query = orderDatabase
                .orderByChild("sellerUserId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists())
                {
                    int totalOrdersCount = 0;
                    int inTransitCount = 0;
                    int completedCount = 0;
                    int cancelledCount = 0;

                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        totalOrdersCount++;

                        Orders orders = dataSnapshot.getValue(Orders.class);


                        String orderStatus = orders.getOrderStatus();

                        if(orderStatus.equals("1"))
                        {
                            inTransitCount++;
                        }
                        else if(orderStatus.equals("2"))
                        {
                            completedCount++;
                        }
                        else if(orderStatus.equals("3"))
                        {
                            cancelledCount++;
                        }


                        double pricePerKilo = orders.getPricePerKilo();
                        int priceInFloat = (int)pricePerKilo;

                        String[] dateCreatedArr = orders.getDateCreated().split("-");
                        String monthCreated = dateCreatedArr[0];

                        switch (monthCreated)
                        {
                            case "Jan":
                                janValue = janValue + priceInFloat;
                                break;

                            case "Feb":
                                febValue = febValue + priceInFloat;
                                break;

                            case "Mar":
                                marValue = marValue + priceInFloat;
                                break;

                            case "Apr":
                                aprValue = aprValue + priceInFloat;
                                break;

                            case "May":
                                mayValue = mayValue + priceInFloat;
                                break;

                            case "Jun":
                                junValue = junValue + priceInFloat;
                                break;

                            case "Jul":
                                julValue = julValue + priceInFloat;
                                break;

                            case "Aug":
                                augValue = augValue + priceInFloat;
                                break;

                            case "Sep":
                                sepValue = sepValue + priceInFloat;
                                break;

                            case "Oct":
                                octValue = octValue + priceInFloat;
                                break;

                            case "Nov":
                                novValue = novValue + priceInFloat;
                                break;

                            case "Dec":
                                decValue = decValue + priceInFloat;
                                break;

                        }



                    }

                    setArrayListValueForChart();

                    tv_totalOrdersCount.setText(totalOrdersCount+"");
                    tv_inTransitCount.setText(inTransitCount+"");
                    tv_completedCount.setText(completedCount+"");
                    tv_cancelledCount.setText(cancelledCount+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setArrayListValueForChart() {

        labels.addAll(Arrays.asList(monthName));

        entries.add(new BarEntry(0, janValue));
        entries.add(new BarEntry(1, febValue));
        entries.add(new BarEntry(2, marValue));
        entries.add(new BarEntry(3, aprValue));
        entries.add(new BarEntry(4, mayValue));
        entries.add(new BarEntry(5, junValue));
        entries.add(new BarEntry(6, julValue));
        entries.add(new BarEntry(7, augValue));
        entries.add(new BarEntry(8, sepValue));
        entries.add(new BarEntry(9, octValue));
        entries.add(new BarEntry(10, novValue));
        entries.add(new BarEntry(11, decValue));

        generateSalesChart();
    }

    private void generateSalesChart() {


        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);

        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // set the x-axis to the bottom
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.getAxisLeft().setGridColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);


        barChart.getLegend().setEnabled(false); // hide the legend
        barChart.animateXY(2000, 1000); // animate the chart
        barChart.invalidate();


    }

    private void generateProductsData() {

        Query query = productDatabase
                .orderByChild("userId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    long totalProductsCount = 0;
                    totalProductsCount = snapshot.getChildrenCount();

                    tv_totalProductsCount.setText(totalProductsCount+"");

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);


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
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setRef() {

        tv_back = findViewById(R.id.tv_back);
        tv_totalSales = findViewById(R.id.tv_totalSales);
        tv_totalOrdersCount = findViewById(R.id.tv_totalOrdersCount);
        tv_totalProductsCount = findViewById(R.id.tv_totalProductsCount);
        tv_inTransitCount = findViewById(R.id.tv_inTransitCount);
        tv_completedCount = findViewById(R.id.tv_completedCount);
        tv_cancelledCount = findViewById(R.id.tv_cancelledCount);

        barChart = findViewById(R.id.barChart);
    }
}