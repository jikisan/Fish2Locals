package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class sign_up_page extends AppCompatActivity {

    private TextView tv_signIn;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        setRef();
        clicks();


    }

    private void clicks() {
        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign_up_page.this, login_page.class);
                startActivity(intent);
            }
        });
    }

    private void setRef() {
        tv_signIn = findViewById(R.id.tv_signIn);

    }
}