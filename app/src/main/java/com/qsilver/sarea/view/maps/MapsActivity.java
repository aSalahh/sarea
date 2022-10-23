package com.qsilver.sarea.view.maps;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsilver.sarea.R;
import com.qsilver.sarea.directionhelpers.TaskLoadedCallback;
import com.qsilver.sarea.model.Travel;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.model.UserLocation;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    User driver = new User();
    ArrayList<String> travelStudents;
    Button startTracking, endTracking;
    ImageView button_refresh;
    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private Travel currentTravel;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private LatLng busStartLocation;
    private LatLng driverLocation;
    private LatLng busEndLocation;
    private Marker mBusStartLocation;
    private Marker mdriverLocation;
    private LatLng userDefaultLocation;
    private Marker studentLocation;
    private Marker mBusEndLocation;
    private Polyline currentPolyline;
    private ArrayList<UserLocation> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        startTracking = findViewById(R.id.button_start);
        endTracking = findViewById(R.id.button_end);
        button_refresh = findViewById(R.id.button_refresh);
        studentList = new ArrayList<UserLocation>();
        // getStudentLocations();
        currentTravel = (Travel) getIntent().getSerializableExtra("MAP_CLICK_DRIVER");
        if (currentTravel != null) {
            busStartLocation = new LatLng(Double.parseDouble(currentTravel.getTravelStartLocation().getLatitude()),
                    Double.parseDouble(currentTravel.getTravelStartLocation().getLongitude()));
            busEndLocation = new LatLng(Double.parseDouble(currentTravel.getTravelEndLocation().getLatitude()),
                    Double.parseDouble(currentTravel.getTravelEndLocation().getLongitude()));
            driverLocation = new LatLng(Double.parseDouble(currentTravel.getDriver().getUserLocation().getLatitude()),
                    Double.parseDouble(currentTravel.getDriver().getUserLocation().getLongitude()));


        }
//        if (LoadData(MapsActivity.this, "USER_TYPE") != null &&
//                LoadData(MapsActivity.this, "USER_TYPE").equals("driver")) {
//
//            startTracking.setVisibility(View.VISIBLE);
//            endTracking.setVisibility(View.VISIBLE);
//        } else {
//            startTracking.setVisibility(View.GONE);
//            endTracking.setVisibility(View.GONE);
//        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);


                } else {
                    stareLocationService();
                }

            }
        });
        endTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        int height = 150;
        int width = 150;
        //bus Icon
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bus_station);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap busIcon = Bitmap.createScaledBitmap(b, width, height, true);
        //start Icon
        BitmapDrawable startBitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.start);
        Bitmap StartB = startBitmapDraw.getBitmap();
        Bitmap StarttravelIcon = Bitmap.createScaledBitmap(StartB, width, height, true);
        //end Icon
        BitmapDrawable endBitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.end);
        Bitmap endB = endBitmapDraw.getBitmap();
        Bitmap endIcon = Bitmap.createScaledBitmap(endB, width, height, true);
        // Add start - End Mark
        mBusStartLocation = mMap.addMarker(new MarkerOptions().position(busStartLocation).title("اللإنطلاق")
                .icon(BitmapDescriptorFactory.fromBitmap(StarttravelIcon)));
        mBusStartLocation.setTag(0);
        mBusEndLocation = mMap.addMarker(new MarkerOptions().position(busEndLocation).title("النهاية")
                .icon(BitmapDescriptorFactory.fromBitmap(endIcon)));
        mBusEndLocation.setTag(0);


        // bus Location
        mdriverLocation = mMap.addMarker(new MarkerOptions().position(driverLocation).title("الحافلة")
                .icon(BitmapDescriptorFactory.fromBitmap(busIcon)));
        mBusEndLocation.setTag(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(busStartLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(busEndLocation));


        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("TravelStudents");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User students = snapshot.getValue(User.class);
                    String StudentTravelId = students.getTravelId();
                    if (StudentTravelId != null && StudentTravelId.equals(currentTravel.getTravelId()) &&
                            students.getTravelType().equals(currentTravel.getType())) {
                        createMarker(Double.parseDouble(students.getUserLocation().getLatitude()),
                                Double.parseDouble(students.getUserLocation().getLongitude()),
                                students.getUserLocation().getAddress());

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        addBusValueEventListener();
        // addStudentValueEventListener();


        //zooming when mao opened
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    // getStudentLocations();

                } catch (Exception e) {
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

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBusValueEventListener() {
        if (mMap == null) return;

        DatabaseReference busReference = FirebaseDatabase.getInstance().getReference("Travel").child(currentTravel.getTravelId()).child("busLocation");


        busReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserLocation locationMarker = dataSnapshot.getValue(UserLocation.class);

                if (locationMarker != null) {

                    LatLng sydney = new LatLng(Double.parseDouble(locationMarker.getLatitude()), Double.parseDouble(locationMarker.getLongitude()));
                    mdriverLocation.setPosition(sydney);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    Marker createMarker(double latitude, double longitude, String title) {
        int height = 150;
        int width = 150;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.student_ic);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, true);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title(title));
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                stareLocationService();
            } else {
                Toast.makeText(this, "رفض الصلاحية", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void stareLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.putExtra("MY_CURRENT_TRAVEL_TYPE", currentTravel.getType());
            intent.putExtra("MY_CURRENT_USER_TYPE", LoadData(MapsActivity.this, "USER_TYPE"));
            intent.putExtra("MY_CURRENT_TRAVEL_ID", currentTravel.getTravelId());
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "تم تفعيل التتبع", Toast.LENGTH_LONG).show();


        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "تم إيقاف التتبع", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}