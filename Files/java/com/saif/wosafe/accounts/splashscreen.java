package com.saif.wosafe.accounts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.saif.wosafe.MainActivity;
import com.saif.wosafe.R;


public class splashscreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean check = Boolean.valueOf(SharedPrefs.readSharedSetting(splashscreen.this,"sharedPrefs","true"));
        Boolean check2 = Boolean.valueOf(SharedPrefs.readSharedSetting(splashscreen.this,"welcomePrefs","true"));
        if(check){
            startActivity(new Intent(splashscreen.this, login.class));
            finish();
        }
        else if(check2) {
            startActivity(new Intent(splashscreen.this, WelcomeActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(splashscreen.this, MainActivity.class));
            finish();
        }
    }


}


