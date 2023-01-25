package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login_page extends AppCompatActivity {

    private TextView tv_signUp, tv_loginBtn;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef();
        clicks();


    }

    private void clicks() {
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_page.this, sign_up_page.class);
                startActivity(intent);
            }
        });

        tv_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {

        String sample = "SAMPLE";

        userDatabase.push().setValue(sample).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(login_page.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef() {
        tv_signUp = findViewById(R.id.tv_signUp);
        tv_loginBtn = findViewById(R.id.tv_loginBtn);
    }
}