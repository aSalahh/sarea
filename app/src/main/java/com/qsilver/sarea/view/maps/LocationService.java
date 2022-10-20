package com.qsilver.sarea.view.maps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.model.UserLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationService extends Service {
    String travelType;
    String travelId;
    String currentUserType;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LatLng latLngA;
    LatLng latLngB;
    Notification n;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("LOCATION_UPDATE", latitude + "," + longitude);


                if (travelType != null && travelId != null) {
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Travel").child(travelId).child("busLocation");
                    myRef.setValue(new UserLocation(String.valueOf(latitude), String.valueOf(longitude), getAddress(latitude, longitude)));
                    myRef.setValue(new UserLocation(String.valueOf(latitude), String.valueOf(longitude), getAddress(latitude, longitude)));

                    if (currentUserType.equals("student")) {
                        DatabaseReference reference1;
                        reference1 = FirebaseDatabase.getInstance().getReference("TravelStudents").child(currentuser);
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User students = dataSnapshot.getValue(User.class);
                                if (students.getTravelId().equals(travelId) && students.getTravelType().equals(travelType)) {
                                    UserLocation userLocation = students.getUserLocation();
                                    double distance = (int) getDistance(latitude, longitude, Double.parseDouble(userLocation.getLatitude())
                                            , Double.parseDouble(userLocation.getLongitude()));
                                    Log.d("DISTANCE", String.valueOf(distance));

                                    if (distance < 2000) {
                                        Log.d("LOCATION_DISTANCE", String.valueOf(distance));

                                        NotificationManager notificationManager = (NotificationManager)
                                                getSystemService(NOTIFICATION_SERVICE);

                                        n = new NotificationCompat.Builder(LocationService.this)
                                                .setContentTitle("سـارع")
                                                .setContentText(" الحافلة على بعد :" + distance + "متر")
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setDefaults(Notification.DEFAULT_SOUND)
                                                .setAutoCancel(true)
                                                .setWhen(System.currentTimeMillis())
                                                .build();

                                        n.flags |= Notification.FLAG_AUTO_CANCEL | Intent.FLAG_ACTIVITY_SINGLE_TOP;
                                        notificationManager.notify(0, n);
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }

                }


            }
        }
    };
    LocationListener listener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not Yet Implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Services");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle(" تم تفعيل التتبع");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("this channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(9000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());

    }

    private void stopLocationServices() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        travelType = intent.getStringExtra("MY_CURRENT_TRAVEL_TYPE");
        travelId = intent.getStringExtra("MY_CURRENT_TRAVEL_ID");
        currentUserType = intent.getStringExtra("MY_CURRENT_USER_TYPE");
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationServices();
                }
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    double getDistance(double lat1, double lon1, double lat2, double lon2) {
        latLngA = new LatLng(lat1, lon1);
        latLngB = new LatLng(lat2, lon2);

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        return locationA.distanceTo(locationB);
    }


}
