package com.edu.tdc.blackbar.tourismguide.myAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.edu.tdc.blackbar.tourismguide.R;
import com.edu.tdc.blackbar.tourismguide.datamodel.ScheduleModel;

import java.util.ArrayList;

/**
 * Created by ASUS on 12/14/2016.
 */

public class ScheduleAdapter extends ArrayAdapter<ScheduleModel> {
    Activity context = null;
    int itemlayout;
    ArrayList<ScheduleModel> schedule = null;
    public ScheduleAdapter(Context context, int resource, ArrayList<ScheduleModel> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        itemlayout = resource;
        schedule = objects;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        convertView = inflater.inflate(itemlayout , null);

        if (schedule.size()> 0 && pos >= 0) {
            final TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);
            final TextView txtDateFrm = (TextView) convertView.findViewById(R.id.txtDateFrom);
            final TextView txtDateTo = (TextView) convertView.findViewById(R.id.txtDateTo);
            final TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            final TextView txtNote = (TextView) convertView.findViewById(R.id.txtNote);

            ScheduleModel sche = schedule.get(pos);
            if(sche != null){
                txtLocation.setText(sche.getLocation());
                txtDateFrm.setText(sche.getDatefrom());
                txtDateTo.setText(sche.getDateto());
                txtTime.setText(sche.getTime());
                txtNote.setText(sche.getNote());
            }

        }

        return convertView;
    }
}
