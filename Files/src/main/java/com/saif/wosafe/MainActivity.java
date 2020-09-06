package com.saif.wosafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.encoders.json.JsonDataEncoderBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.saif.wosafe.accounts.SharedPrefs;
import com.saif.wosafe.accounts.UserProfile;
import com.saif.wosafe.accounts.login;
import com.saif.wosafe.accounts.splashscreen;
import com.saif.wosafe.data.Data;
import com.saif.wosafe.data.DataModel;
import com.saif.wosafe.data.Notification;
import com.saif.wosafe.home.GeoPointData;
import com.saif.wosafe.notifications.NotificationAdapter;
import com.saif.wosafe.notifications.NotificationData;
import com.saif.wosafe.settings.ContactActivity;
import com.saif.wosafe.settings.DataSaverActivity;
import com.saif.wosafe.settings.LocationService;
import com.saif.wosafe.settings.PrivacyAndSecurityActivity;
import com.saif.wosafe.settings.WebViewActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static android.telephony.CellLocation.requestLocationUpdate;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, TileProvider{

    private String Saif = "Tag";
    RelativeLayout home,dashboard,notification;
    ScrollView settings;
    double lat=0.0,lng=0.0;
    GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    RelativeLayout profileView,privacyAndSafety,appUpdate,language,contactsUpdate,dataSaver,subscribe,unsubscribe;
    RelativeLayout helpCenter,safetyCenter,reportBugs,termOfUse,communityGuidelines,dataPolicy,logout;

    ArrayList<NotificationData> notificationDataArrayList = new ArrayList<>();
    TextView userName,location2,activeUser,userInTrouble;
    CircleImageView userPic;
    Button locationUpdate;
    GeoPoint latLng;
    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;

    LocationManager locationManager;
    List<LatLng> list =new ArrayList<LatLng>();
    GeoPoint geoPoint;
    LatLng latLng2;

    public static String documentId;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    home.setVisibility(View.VISIBLE);
                    dashboard.setVisibility(View.GONE);
                    notification.setVisibility(View.GONE);
                    settings.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    home.setVisibility(View.GONE);
                    dashboard.setVisibility(View.VISIBLE);
                    notification.setVisibility(View.GONE);
                    settings.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    home.setVisibility(View.GONE);
                    dashboard.setVisibility(View.GONE);
                    notification.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_settings:
                    home.setVisibility(View.GONE);
                    dashboard.setVisibility(View.GONE);
                    notification.setVisibility(View.GONE);
                    settings.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPrefs.saveSharedSetting(MainActivity.this,"welcomePrefs","false");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    Log.d(Saif,"onLocationResult: location error");
                    return;
                }
                Location location = locationResult.getLastLocation();
                lat = location.getLatitude();
                lng = location.getLongitude();
                LatLng latLng = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                final GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                final GeoPointData geoPointData = new GeoPointData(geoPoint);
                location2.setText("Location Updated");
                try {
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getLocationUpdate();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        location2 = findViewById(R.id.Location);
        home = findViewById(R.id.HomeScreen);
        dashboard = findViewById(R.id.DashboardScreen);
        notification = findViewById(R.id.NotificationScreen);
        settings = findViewById(R.id.SettingsScreen);
        activeUser = findViewById(R.id.ActiveUser);
        userInTrouble = findViewById(R.id.UserInTrouble);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Home();
        Dashboard();
        Notification();
        Settings();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(Saif, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(Saif, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    private void getLocationUpdate() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
    }

    private void Notification(){
        final ListView listView = findViewById(R.id.NotificationListView);
        final TextView notificationText = findViewById(R.id.NotificationText);
        db.collection("Notification")
                .whereEqualTo("userUid",user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        NotificationData notificationData;
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            notificationData = new NotificationData(documentSnapshot.get("userProfile").toString(), documentSnapshot.get("notificationType").toString(),documentSnapshot.get("notificationText").toString(),documentSnapshot.get("notificationTime").toString(),documentSnapshot.getString("userUid"));
                            notificationDataArrayList.add(notificationData);
                        }
//                        Collections.sort(notificationDataArrayList, new Comparator<NotificationData>() {
//                            @SuppressLint("SimpleDateFormat")
//                            DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
//                            @Override
//                            public int compare(NotificationData notificationData, NotificationData t1) {
//                                try {
//                                    return f.parse(notificationData.getNotificationTime()).compareTo(f.parse(t1.getNotificationTime()));
//                                }catch (ParseException e){
//                                    throw new IllegalArgumentException(e);
//                                }
//                            }
//                        });
                        if(!notificationDataArrayList.isEmpty()) {
                            notificationText.setVisibility(View.GONE);
                            listView.setAdapter(new NotificationAdapter(MainActivity.this, notificationDataArrayList));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        String notification = notificationDataArrayList.get(i).getNotificationText();
                                        notification = notification.substring(47);
                                        StringBuilder str = new StringBuilder();
                                        str.append(notification);
                                        notification = str.reverse().toString();
                                        notification = notification.substring(47);
                                        StringBuilder str1 = new StringBuilder();
                                        str1.append(notification);
                                        notification = str1.reverse().toString();
                                        Log.d(Saif, notification);
                                        Toast.makeText(MainActivity.this,notification,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(notification));
                                        startActivity(intent);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, int i, long l) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Do you want to Delete this Notification ?");
                                    builder.setTitle("Notification");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            try {
                                                adapterView.removeView(view);
                                                notifyAll();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                    return true;
                                }
                            });
                        }
                        else{
                            notificationText.setText("No new Notification available");
                            notificationText.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void Settings(){
        userName = findViewById(R.id.Settings_UserName);
        userPic = findViewById(R.id.Settings_UserImage);
        try {
            db.collection("users")
                    .whereEqualTo("userUid",firebaseAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot documentSnapshot : documentSnapshots){
                                documentId = documentSnapshot.getId();
                            }
                            try {
                                db.collection("users")
                                        .document(documentId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                userName.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                                                Glide.with(MainActivity.this).load(Uri.parse(Objects.requireNonNull(documentSnapshot.get("profilePic")).toString())).into(userPic);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }catch (Exception e) {
            e.printStackTrace();
        }

        profileView = findViewById(R.id.setting_rl1);
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

        privacyAndSafety = findViewById(R.id.Under_Account_rl1);
        privacyAndSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrivacyAndSecurityActivity.class);
                startActivity(intent);
            }
        });

        appUpdate = findViewById(R.id.Under_General_rl1);
        appUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName)));
                }catch (android.content.ActivityNotFoundException e){
                    e.printStackTrace();
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/app/details?="+appPackageName)));
                }
            }
        });
        language = findViewById(R.id.Under_General_rl2);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.settings","com.android.settings.LanguageSettings");
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        contactsUpdate = findViewById(R.id.Under_General_rl3);
        contactsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });
        dataSaver = findViewById(R.id.Under_General_rl4);
        dataSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataSaverActivity.class);
                startActivity(intent);
            }
        });
        subscribe = findViewById(R.id.Under_General_rl5);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().subscribeToTopic("hally")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(Saif, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        unsubscribe = findViewById(R.id.Under_General_rl6);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("hally")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_unsubscribed);
                                if(!task.isSuccessful()){
                                    msg = getString(R.string.msg_unsubscribe_failed);
                                }
                                Log.d(Saif,msg);
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        helpCenter = findViewById(R.id.Under_Support_rl1);
        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        safetyCenter = findViewById(R.id.Under_Support_rl2);
        safetyCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        reportBugs = findViewById(R.id.Under_Support_rl3);
        reportBugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        termOfUse = findViewById(R.id.Under_About_rl1);
        termOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        communityGuidelines = findViewById(R.id.Under_About_rl2);
        communityGuidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        dataPolicy = findViewById(R.id.Under_About_rl3);
        dataPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        logout = findViewById(R.id.Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SharedPrefs.saveSharedSetting(MainActivity.this,"sharedPrefs","true");
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void Home(){
        requestLocationUpdate();
        getLocation();
        final ProgressBar progressBar = findViewById(R.id.HomeProgressBar);
        locationUpdate = findViewById(R.id.LocationUpdate);
        locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert vibrator != null;
                vibrator.vibrate(1000);
                Toast.makeText(MainActivity.this,"Distress Triggered",Toast.LENGTH_LONG).show();
                requestLocationUpdate();
                getLocation();
                progressBar.setVisibility(View.VISIBLE);
                final String message = "I'm in trouble,\nPlease help me.\nMy location is https://maps.google.com/maps?daddr="+lat+","+lng+"\nMessage from Hally, please do help.\nThank you.";
                try {
                    db.collection("users")
                            .whereEqualTo("userUid",firebaseAuth.getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                        documentId = documentSnapshot.getId();
                                        ArrayList<String> numbers = (ArrayList<String>) documentSnapshot.get("numberList");
                                        assert numbers != null;
                                        for(int i=0;i<numbers.size();i++){
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(numbers.get(i),null,message,null,null);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        String profilePic = Objects.requireNonNull(documentSnapshot.get("profilePic")).toString();
                                        Calendar e = Calendar.getInstance();
                                        Date date = e.getTime();
                                        String notificationIconUrl = "https://firebasestorage.googleapis.com/v0/b/wosafe-5e1fc.appspot.com/o/notification_icon%2Fnotification_icon.png?alt=media&token=4b204bf6-9485-420b-b4c6-58a060d635a0";
                                        String title = "Flash";
                                        String body = "I'm in Trouble";
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(ApiInterface.URL_BASE)
                                                .addConverterFactory(ScalarsConverterFactory.create())
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();

                                        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                                        DataModel dataModel = new DataModel("/topics/hally",new Data(profilePic,notificationIconUrl,message,date.toString()),new Notification(title,body,profilePic,"flash_tone"));
                                        Call<JsonObject> response = apiInterface.submit(dataModel);
                                        response.enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                Toast.makeText(MainActivity.this,"API Called",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                                Toast.makeText(MainActivity.this,"API not Called",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }}).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                GeoPoint geoPoint = new GeoPoint(lat,lng);
                GeoPointData geoPointData = new GeoPointData(geoPoint);
                try {
                    db.collection("distress")
                            .document(firebaseAuth.getUid())
                            .set(geoPointData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });

    }

    public interface ApiInterface {

        String URL_BASE = "https://fcm.googleapis.com/";
        @Headers({"Authorization: key=AAAAVFtCVoU:APA91bF8QJnWOeZc3p3p4eXQmz7wl34RuOIK_ktRdagj1i87flZXlskJQKLOZBJPbG6RZjXfwZuLWuU5yz4mGMMS4kOv0x5za5mSJbBo054689pbstkvUbQe7yCGRwKbQmmpVT_3CP9N","Content-Type: application/json"})
        @POST("fcm/send")
        Call<JsonObject> submit(@Body DataModel body);

    }

    private void Dashboard(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.GoogleMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(MainActivity.this);
        ActiveUser();
        UserInTrouble();
        addHeatMap();
    }



    private void ActiveUser(){
        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        int count =0;
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            count = count + 1;
                        }
                        activeUser.setText(""+count);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void UserInTrouble(){
        db.collection("distress")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        int count =0;
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            count = count + 1;
                        }
                        userInTrouble.setText(""+count);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        location2.setText("Location Updated");
        LatLng latLng = new LatLng(lat,lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
        final GeoPointData geoPointData = new GeoPointData(geoPoint);
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private  void addHeatMap(){
        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        int count =0;
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            geoPoint = (GeoPoint) documentSnapshot.get("latLng");
                            assert geoPoint != null;
                            latLng2 = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng2));
                            Log.d(Saif,""+latLng2.latitude);
                            list.add(latLng2);
                            count = count + 1;
                        }
                        activeUser.setText(""+count);
                        int[] colors = {
                                Color.rgb(102,255,0),
                                Color.rgb(255,0,0)
                        };

                        float[] startPoints = {
                                0.2f,1f
                        };

                        Gradient gradient = new Gradient(colors,startPoints);
                        mProvider = new HeatmapTileProvider.Builder()
                                .data(list)
                                .gradient(gradient)
                                .build();
                        mOverlay = mMap.addTileOverlay(new
                                TileOverlayOptions().tileProvider(mProvider));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Tile getTile(int i, int i1, int i2) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

}
