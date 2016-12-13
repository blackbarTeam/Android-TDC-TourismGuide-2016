package com.edu.tdc.blackbar.tourismguide;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {
    private EditText edtTimeFrom, edtTimeTo, edtDateFrom, edtDateTo, edtLocation, edtNote;
    private ImageButton imgCheck, imgCancle, imgCalendar;
    private Switch swCount;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        edtDateFrom = (EditText) findViewById(R.id.edtDateFrom);
        edtDateTo = (EditText) findViewById(R.id.edtDateTo);
        edtTimeFrom = (EditText) findViewById(R.id.edtTimeFrom);
        edtTimeTo = (EditText) findViewById(R.id.edtTimeTo);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        edtNote = (EditText) findViewById(R.id.edtNote);
        imgCheck = (ImageButton) findViewById(R.id.imgCheck);
        imgCancle = (ImageButton) findViewById(R.id.imgCancle);
        imgCalendar = (ImageButton) findViewById(R.id.imgCalendar);
        swCount = (Switch) findViewById(R.id.swCountDown);




        //ham goi edittext de bat dau dem nguoc

        //Calendar ( LUU Y: CHUA SU DUNG DUOC DO VAN DE VE API CUA ANDROID )
        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast to = Toast.makeText(ScheduleActivity.this, "NOT RIGHT NOW", Toast.LENGTH_LONG);
                to.show();
            }
        });


        //ham dem nguoc su dung edittext, sau khi bat slide button
        swCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast toast = Toast.makeText(ScheduleActivity.this, "Countdown ON", Toast.LENGTH_SHORT);
                    toast.show();
                    CountDownTimer count = new CountDownTimer(30000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Toast toast1 = Toast.makeText(ScheduleActivity.this, "Countdown start", Toast.LENGTH_SHORT);
                            toast1.show();
                            String a = edtDateFrom.getText().toString();
                            String b =edtDateTo.getText().toString();

//                            Log.d("countdown",a);
//                            Log.d("countdown",b);

                        }

                        @Override
                        public void onFinish() {
                            Toast fin = Toast.makeText(ScheduleActivity.this, "Done" , Toast.LENGTH_SHORT);
                        }
                    }.start();
                }
                else {
                    Toast toasta = Toast.makeText(ScheduleActivity.this, "Countdown OFF", Toast.LENGTH_SHORT);
                    toasta.show();
                }
            }
        });



        //button check
        imgCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                edtDateTo.getText().toString().trim();
                edtDateFrom.getText().toString().trim();
                //khoi tao notification
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                PendingIntent broadcast = PendingIntent.getBroadcast(ScheduleActivity.this, 50, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //ham goi Alarm
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 5);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);


            }
        });

        //intent tra ve main
        imgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                startActivity(intent);
                onBackPressed();

            }
        });


    }







}
