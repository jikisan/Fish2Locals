package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class view_privacy_and_policy_page extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_privacy_and_policy_page);

        WebView webView = findViewById(R.id.wv_webView);
        TextView tv_back = findViewById(R.id.tv_back);

        webView.getSettings().setJavaScriptEnabled(true);
        String privacyHtml = "Privacy_and_policy.html";
        webView.loadUrl("file:///android_asset/" + privacyHtml);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}