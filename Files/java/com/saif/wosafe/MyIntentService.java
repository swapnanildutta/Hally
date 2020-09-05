package com.saif.wosafe;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

public class MyIntentService extends IntentService {
    public static final String TAG = MyIntentService.class.getSimpleName();


    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
