package com.edu.tdc.blackbar.tourismguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.edu.tdc.blackbar.tourismguide.datamodel.ScheduleDB;
import com.edu.tdc.blackbar.tourismguide.datamodel.ScheduleModel;
import com.edu.tdc.blackbar.tourismguide.myAdapter.ScheduleAdapter;

import java.util.ArrayList;

/**
 * Created by ASUS on 12/14/2016.
 */

public class ScheduleMainActivity extends Activity {
    ImageButton imgNew, imgEdit;
    private Button btn_back_details;
    Button btnSave;

    ListView lstSchedule;
    static ArrayList<ScheduleModel> scheArr = new ArrayList<ScheduleModel>();
    ScheduleAdapter schAdap = null;
    ScheduleModel sch;
    Intent intent;
    ScheduleDB db;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main_layout);
        imgNew = (ImageButton) findViewById(R.id.imgAddNew);
        imgEdit = (ImageButton) findViewById(R.id.imgEdit);
        db = new ScheduleDB(getApplicationContext());
        db.createOrOpenDatabase();
        lstSchedule = (ListView) findViewById(R.id.lstSchedule);
        btn_back_details = (Button) findViewById(R.id.btn_back_details);
        btnSave = (Button) findViewById(R.id.btnSave);
        schAdap = new ScheduleAdapter(ScheduleMainActivity.this, R.layout.schedule_detail_layout, scheArr);


        //lay du lieu tu ben lop scheduleactivity ve
        intent = getIntent();
        Bundle data = new Bundle();
        if(intent != null){
            data = intent.getBundleExtra("data");
            if(data != null){
                //get du lieu ve
                sch = new ScheduleModel();
                sch.setLocation(data.getString("Location"));
                sch.setDatefrom(data.getString("DateFrm"));
                sch.setDateto(data.getString("DateTo"));
                sch.setTime(data.getString("Time"));
                sch.setNote(data.getString("Note"));
                scheArr.add(sch);
                schAdap.notifyDataSetChanged();
            }else {
                scheArr.clear();

                //doc list schedule
                //dc.readSheduleList(scheArr);
            }

        }
        lstSchedule.setAdapter(schAdap);


//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dc.writeSheduleList(scheArr);
//            }
//        });

        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleMainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        btn_back_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });



    }
}
