package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class intro_page extends AppCompatActivity {

    private FirebaseUser user;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        progressbar = findViewById(R.id.progressbar);


        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(!(user == null))
                {
                    Intent intent = new Intent(intro_page.this, homepage.class);
                    startActivity(intent);
                    finish();

                }else{

                    Intent intent;
                    intent = new Intent(intro_page.this, login_page.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, 2000);
    }
}