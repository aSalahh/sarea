package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.HelperMethod.isNetworkAvailable;
import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.Travel;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TravelSettingActivity extends AppCompatActivity {
    static final int PICK_MAP_POINT_REQUEST = 999;  // The request code
    int hour, minute;
    ToggleButton tL;
    ToggleButton tM;
    ToggleButton tMi;
    ToggleButton tJ;
    String type ="";

    ToggleButton tV;
    ToggleButton tS;
    RadioButton travelTypeRadioButton;
    RadioGroup travelType;
    double travelStartLong=0.0;
    double travelEndLong=0.0;
    double travelEndLat=0.0;
    double travelStartLat=0.0;
    TextView btnSetStartLocation;
    TextView btnSetEndLocation;
    Geocoder geocoder;
    ProgressBar progressBar;
    ImageButton back;
    private EditText startLocation, endLocation;
    private CardView setTime;
    private CardView selectStudents;
    private CardView selectDriver;
    private CardView submitTravel;
    private TextView myTime;
    User selectedDriver;
    UserLocation startUserLocation;
    UserLocation endUserLocation;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    String currentSchoolId;
    List<User> travelStudents;
    String markedButtons= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_setting);
        setTime = findViewById(R.id.travel_setting_set_time);
        myTime = findViewById(R.id.tv_set_time);
        startLocation = findViewById(R.id.et_set_start_location);
        endLocation = findViewById(R.id.et_set_end_location);
        submitTravel = findViewById(R.id.btn_submit_travel);
        btnSetStartLocation = findViewById(R.id.btn_set_start_location);
        btnSetEndLocation = findViewById(R.id.btn_set_end_location);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        progressBar = findViewById(R.id.travel_setting_progressbar);
        selectDriver= findViewById(R.id.choose_driver);
        back=findViewById(R.id.setting_btn_back);
        selectedDriver=(User) getIntent().getSerializableExtra("CHOSEN_DRIVER_INTENT");
        currentSchoolId = LoadData(TravelSettingActivity.this, "CurrentSchoolId");
        travelStudents=new ArrayList<>();
        tL = (ToggleButton) findViewById(R.id.tL);
        tM = (ToggleButton) findViewById(R.id.tM);
        tMi = (ToggleButton) findViewById(R.id.tMi);
        tJ = (ToggleButton) findViewById(R.id.tJ);
        tV = (ToggleButton) findViewById(R.id.tV);
        tS = (ToggleButton) findViewById(R.id.tS);

        travelType = (RadioGroup) findViewById(R.id.go_back);



        //Check individual items.

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSetStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLocationText = startLocation.getText().toString();
                if (startLocationText.equals("") && startLocationText.length() == 0) {
                    Toast.makeText(TravelSettingActivity.this, "من فضلك أدخل مكان بداية الرحلة", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    List addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(startLocationText, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = (Address) addressList.get(0);
                            // StringBuilder stringBuilder=new StringBuilder();
                            startUserLocation=new UserLocation(String.valueOf(address.getLatitude()),String.valueOf(address.getLongitude()),startLocationText);
                            travelStartLat = address.getLatitude();
                            travelStartLong = address.getLongitude();
                            Toast.makeText(TravelSettingActivity.this, "تم تحديد " + startLocationText , Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(TravelSettingActivity.this, "من فضلك أدخل اسم الموقع صحيح", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                }


            }
        });
        selectDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TravelSettingActivity.this,DriversActivity.class);
                startActivity(i);
            }
        });
        btnSetEndLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endLocationText = endLocation.getText().toString();
                if (endLocationText.equals("") && endLocationText.length() == 0) {
                    Toast.makeText(TravelSettingActivity.this,"من فضلك أدخل مكان نهاية الرحلة", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    List addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(endLocationText, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = (Address) addressList.get(0);
                            // StringBuilder stringBuilder=new StringBuilder();
                            endUserLocation=new UserLocation(String.valueOf(address.getLatitude()),String.valueOf(address.getLongitude()),endLocationText);

                            travelEndLat = address.getLatitude();
                            travelEndLong = address.getLongitude();
                            Toast.makeText(TravelSettingActivity.this, "تم تحديد " + endLocationText , Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(TravelSettingActivity.this, "من فضلك أدخل اسم الموقع صحيح", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                }


            }
        });
        submitTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();

            }
        });
        //   selectStudents=findViewById(R.id.choose_student);
//        selectStudents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(TravelSettingActivity.this,StudentActivity.class);
//                startActivity(i);
//            }
//        });


        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });
//        startLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            //    pickPointOnMap();
//            }
//        });


    }




    private void checkValidation() {
        if(tL.isChecked()){
            markedButtons +="الخميس,";
        }
        if(tM.isChecked()){
            markedButtons +="الأربعاء,";
        }
        if(tMi.isChecked()){
            markedButtons +="الثلاثاء,";
        }
        if(tJ.isChecked()){
            markedButtons +="الإثنين,";
        }
        if(tV.isChecked()){
            markedButtons +="الأحد,";
        }
        if(tS.isChecked()){
            markedButtons +="السبت";
        }
        String time = myTime.getText().toString();
        int selectedId = travelType.getCheckedRadioButtonId();
        travelTypeRadioButton = (RadioButton) findViewById(selectedId);
        type=String.valueOf(travelTypeRadioButton.getText());

        if (time.equals("") || time.length() == 0||
                type.equals("") || type.length() == 0||
                markedButtons.equals("") || markedButtons.length() == 0||
                travelEndLat == 0.0 || travelEndLong == 0.0 ||
                travelStartLat == 0.0 || travelStartLong == 0.0 ||

               selectDriver ==null ) {
            Toast.makeText(TravelSettingActivity.this, "من فضلك إملئ جميع الحقول", Toast.LENGTH_LONG).show();
        }  else if (!isNetworkAvailable(TravelSettingActivity.this)) {
            Toast.makeText(TravelSettingActivity.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();

        }

        else {
            createTravel(selectedDriver,time,startUserLocation,endUserLocation,markedButtons,type);



        }
    }
    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(99999999);
        return String.format("%09d", number);
    }
    private void createTravel(User driver,String time,UserLocation startTravelLocation ,
                              UserLocation EndTravelLocation,String days,String type) {
        // save in users root
        String travelId=getRandomNumberString();
            reference = FirebaseDatabase.getInstance().getReference("Travel").child(travelId);

        Travel travel=new Travel(driver,time,startTravelLocation,EndTravelLocation,currentSchoolId,days,travelId,type,startTravelLocation);
        reference.setValue(travel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent goToMainActivity = new Intent(TravelSettingActivity.this, MainActivity.class);
                    goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goToMainActivity);
                    overridePendingTransition(R.anim.left_enter, R.anim.right_out);
                    Toast.makeText(TravelSettingActivity.this, "تم", Toast.LENGTH_LONG).show();
                }


            }

        });

    }




    public void popTimePicker() {

        TimePickerDialog Tp = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myTime.setText(hourOfDay + ":" + minute);

            }
        }, hour, minute, false);
        Tp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Tp.show();

    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            double startLatitude = 0.0;
            double endLatitude = 0.0;
            double startLongitude = 0.0;
            double endLongitude = 0.0;
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 1:
                    startLatitude = bundle.getDouble("lat");
                    startLongitude = bundle.getDouble("lon");
                    break;
                case 2:
                    endLatitude = bundle.getDouble("lat");
                    endLongitude = bundle.getDouble("lon");
                    break;
                default:
                    startLatitude = 0.0;
                    endLatitude = 0.0;
                    startLongitude = 0.0;
                    endLongitude = 0.0;
            }
            travelEndLat = endLatitude;
            travelEndLong = endLongitude;
            travelStartLat = startLatitude;
            travelStartLong = startLongitude;
            progressBar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(TravelSettingActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}