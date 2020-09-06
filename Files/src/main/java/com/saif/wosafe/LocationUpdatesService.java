package com.saif.wosafe;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.saif.wosafe.home.GeoPointData;

import java.util.List;

public class LocationUpdatesService extends Service {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    private static final String CHANNEL_ID = "channel_01";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int NOTIFICATION_ID = 12345678;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    public LocationUpdatesService() {
    }

    public static String documentId;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    Log.d(TAG,"onLocationResult: location error");
                    return;
                }
                Location location = locationResult.getLastLocation();
                final GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                final GeoPointData geoPointData = new GeoPointData(geoPoint);
                //Toast.makeText(MainActivity.this, Utils.getLocationText(location), Toast.LENGTH_SHORT).show();
                db.collection("users")
                        .whereEqualTo("userUid",firebaseAuth.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                    documentId = documentSnapshot.getId();
                                    db.collection("users")
                                            .document(documentId)
                                            .set(geoPointData, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //Toast.makeText(MainActivity.this,"Location Updated",Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(MainActivity.this,"Location Update failure",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID,getNotification());
        getLocationUpdate();
        return START_NOT_STICKY;
    }

    private void getLocationUpdate() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);
        stopService(intent);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),servicePendingIntent)
                .setContentText("Location update service is running.")
                .setContentTitle("Location Update")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);// Channel ID
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
