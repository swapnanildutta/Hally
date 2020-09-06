package com.saif.wosafe.settings;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.saif.wosafe.R;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
}
